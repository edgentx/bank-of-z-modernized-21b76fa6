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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

  private ScreenMapAggregate aggregate;
  private RenderScreenCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_ScreenMap_aggregate() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @And("a valid screenId is provided")
  public void a_valid_screenId_is_provided() {
    // Command construction happens in the 'When' step to allow variations
  }

  @And("a valid deviceType is provided")
  public void a_valid_deviceType_is_provided() {
    // Command construction happens in the 'When' step
  }

  @When("the RenderScreenCmd command is executed")
  public void the_RenderScreenCmd_command_is_executed() {
    // Default valid command context
    cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "3270", Map.of("USER", "test"));
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a screen.rendered event is emitted")
  public void a_screen_rendered_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

    ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals("LOGIN_SCREEN", event.screenId());
  }

  // --- Error Scenarios ---

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-map-2");
  }

  @When("the RenderScreenCmd command is executed")
  public void the_RenderScreenCmd_command_is_executed_with_invalid_data() {
    // Scenario context implies we are testing the violation state
    // For 'mandatory fields' violation, we pass null/blank IDs
    cmd = new RenderScreenCmd("screen-map-2", null, "3270", Map.of());
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalArgumentException);
  }

  // --- BMS Constraints Scenario ---

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
    aggregate = new ScreenMapAggregate("screen-map-3");
  }

  @When("the RenderScreenCmd command is executed")
  public void the_RenderScreenCmd_command_is_executed_with_long_data() {
    // Create a string longer than 80 chars
    String longString = "A".repeat(100);
    cmd = new RenderScreenCmd("screen-map-3", "DATA_ENTRY", "3270", Map.of("LONG_FIELD", longString));
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }
}
