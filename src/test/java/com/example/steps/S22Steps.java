package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.ScreenMap;
import com.example.domain.userinterfacenavigation.model.ValidateScreenInputCmd;
import com.example.domain.userinterfacenavigation.model.ScreenInputValidatedEvent;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMap aggregate;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_screen_map_aggregate() {
    aggregate = new ScreenMap("SCREEN1");
    aggregate.addFieldDefinition("ACCOUNT", 10, true);
    aggregate.addFieldDefinition("AMOUNT", 12, true);
  }

  @Given("a valid screenId is provided")
  public void a_valid_screen_id_is_provided() {
    // This is handled implicitly in the 'When' step by constructing the command
  }

  @Given("a valid inputFields is provided")
  public void a_valid_input_fields_is_provided() {
    // This is handled implicitly in the 'When' step
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void the_validate_screen_input_cmd_command_is_executed() {
    var fields = new HashMap<String, String>();
    fields.put("ACCOUNT", "1234567890");
    fields.put("AMOUNT", "100.00");
    
    try {
      resultEvents = aggregate.execute(new ValidateScreenInputCmd("SCREEN1", fields));
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void a_input_validated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    assertEquals("input.validated", resultEvents.get(0).type());
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_screen_map_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMap("SCREEN1");
    aggregate.addFieldDefinition("ACCOUNT", 10, true); // Mandatory
  }

  @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
  public void the_command_executed_with_missing_fields() {
    // Missing ACCOUNT
    var fields = new HashMap<String, String>(); 
    try {
      aggregate.execute(new ValidateScreenInputCmd("SCREEN1", fields));
    } catch (IllegalStateException e) {
      thrownException = e;
    }
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_screen_map_aggregate_that_violates_field_lengths() {
    aggregate = new ScreenMap("SCREEN1");
    aggregate.addFieldDefinition("ACCOUNT", 5, true); // Max length 5
  }

  @When("the ValidateScreenInputCmd command is executed with invalid lengths")
  public void the_command_executed_with_invalid_lengths() {
    var fields = new HashMap<String, String>();
    fields.put("ACCOUNT", "1234567890"); // Length 10 > 5
    try {
      aggregate.execute(new ValidateScreenInputCmd("SCREEN1", fields));
    } catch (IllegalStateException e) {
      thrownException = e;
    }
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
  }
}
