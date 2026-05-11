package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

  private ScreenMapAggregate aggregate;
  private RenderScreenCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid ScreenMap aggregate")
  public void aValidScreenMapAggregate() {
    this.aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @And("a valid screenId is provided")
  public void aValidScreenIdIsProvided() {
    // We defer command creation to When to allow invalid state testing
  }

  @And("a valid deviceType is provided")
  public void aValidDeviceTypeIsProvided() {
  }

  @When("the RenderScreenCmd command is executed")
  public void theRenderScreenCmdCommandIsExecuted() {
    // Default valid values unless overridden by specific violation steps (not easily possible without context injection, 
    // but we assume valid here for the happy path. Error paths will construct specific commands in step definitions below).
    if (cmd == null) {
      cmd = new RenderScreenCmd("SCRN01", "3270");
    }
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a screen.rendered event is emitted")
  public void aScreenRenderedEventIsEmitted() {
    Assertions.assertNotNull(resultEvents);
    Assertions.assertEquals(1, resultEvents.size());
    Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
    Assertions.assertEquals("screen.rendered", event.type());
    Assertions.assertEquals("screen-map-1", event.aggregateId());
  }

  // -- Negative Scenarios --

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
    this.aggregate = new ScreenMapAggregate("screen-map-2");
    // Intentionally blank screenId to violate validation
    this.cmd = new RenderScreenCmd("", "3270");
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void aScreenMapAggregateThatViolatesFieldLengths() {
    this.aggregate = new ScreenMapAggregate("screen-map-3");
    // Intentionally long screenId (>8) to violate BMS constraints
    this.cmd = new RenderScreenCmd("VERY_LONG_SCREEN_NAME", "3270");
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    Assertions.assertNotNull(capturedException);
    // In Java, Domain Rules are often enforced via Exceptions (IllegalArgumentException, IllegalStateException)
    Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
  }

  // Specific violation helpers
  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission. with null screenId")
  public void setupNullScreenIdViolation() {
    this.aggregate = new ScreenMapAggregate("screen-map-4");
    this.cmd = new RenderScreenCmd(null, "3270");
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period. with long deviceType")
  public void setupLongDeviceTypeViolation() {
    this.aggregate = new ScreenMapAggregate("screen-map-5");
    this.cmd = new RenderScreenCmd("SCRN01", "MOBILE"); // 6 chars > 4
  }
}
