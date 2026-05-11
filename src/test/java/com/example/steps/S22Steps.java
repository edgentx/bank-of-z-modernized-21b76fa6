package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMapAggregate aggregate;
  private String screenId;
  private Map<String, String> inputFields;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_ScreenMap_aggregate() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @And("a valid screenId is provided")
  public void a_valid_screenId_is_provided() {
    // Using a screenId defined in the Aggregate stub constraints (MAIN_MENU)
    this.screenId = "MAIN_MENU";
  }

  @And("a valid inputFields is provided")
  public void a_valid_inputFields_is_provided() {
    // MAIN_MENU expects ACTION_CODE with max length 4. "HELP" is valid.
    this.inputFields = Map.of("ACTION_CODE", "HELP");
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void the_ValidateScreenInputCmd_command_is_executed() {
    try {
      var cmd = new ValidateScreenInputCmd(screenId, inputFields);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void a_input_validated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertEquals(InputValidatedEvent.class, resultEvents.get(0).getClass());
    
    var event = (InputValidatedEvent) resultEvents.get(0);
    assertEquals("input.validated", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-map-2");
    // LOGIN_SCREEN requires USER_ID. Providing empty/null map violates this.
    this.screenId = "LOGIN_SCREEN";
    this.inputFields = Map.of(); // Empty map
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalArgumentException);
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_ScreenMap_aggregate_that_violates_field_lengths() {
    aggregate = new ScreenMapAggregate("screen-map-3");
    // MAIN_MENU ACTION_CODE max length is 4. Providing "EXCEED" (6 chars) violates this.
    this.screenId = "MAIN_MENU";
    this.inputFields = Map.of("ACTION_CODE", "EXCEED");
  }
}