package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

  private ScreenMapAggregate aggregate;
  private String screenId;
  private String deviceType;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid ScreenMap aggregate")
  public void aValidScreenMapAggregate() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @And("a valid screenId is provided")
  public void aValidScreenIdIsProvided() {
    this.screenId = "LOGIN01";
  }

  @And("a valid deviceType is provided")
  public void aValidDeviceTypeIsProvided() {
    this.deviceType = "3270_TERMINAL";
  }

  @When("the RenderScreenCmd command is executed")
  public void theRenderScreenCmdCommandIsExecuted() {
    try {
      RenderScreenCmd cmd = new RenderScreenCmd(screenId, deviceType);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a screen.rendered event is emitted")
  public void aScreenRenderedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals("screen-map-1", event.aggregateId());
    assertEquals("LOGIN01", event.screenId());
    assertEquals("3270_TERMINAL", event.deviceType());
    assertNull(thrownException);
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void aScreenMapAggregateThatViolatesMandatoryFields() {
    aggregate = new ScreenMapAggregate("screen-map-err-1");
    this.screenId = ""; // Violation: blank
    this.deviceType = null; // Violation: null
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalArgumentException);
    assertTrue(thrownException.getMessage().contains("cannot be null or blank"));
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void aScreenMapAggregateThatViolatesBmsLengthConstraints() {
    aggregate = new ScreenMapAggregate("screen-map-bms-err");
    // ScreenMapAggregate enforces MAX_SCREEN_ID_LENGTH = 8
    this.screenId = "OVERLONG_SCREEN_ID"; // Length 18
    this.deviceType = "TERMINAL";
  }
}
