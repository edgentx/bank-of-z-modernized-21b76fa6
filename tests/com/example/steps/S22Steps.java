package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMapAggregate aggregate;
  private ValidateScreenInputCmd cmd;
  private Exception thrownException;
  private List<DomainEvent> resultingEvents;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_screen_map_aggregate() {
    aggregate = new ScreenMapAggregate("screen-map-s22");
  }

  @And("a valid screenId is provided")
  public void a_valid_screen_id_is_provided() {
    // screenId is supplied in 'When'
  }

  @And("a valid inputFields is provided")
  public void a_valid_input_fields_is_provided() {
    // inputFields are supplied in 'When'
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_screen_map_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-map-s22-mandatory-fail");
    aggregate.setMandatoryFieldsValidated(false);
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_screen_map_aggregate_that_violates_bms_field_lengths() {
    aggregate = new ScreenMapAggregate("screen-map-s22-bms-fail");
    aggregate.setBmsFieldLengthCompliant(false);
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void the_validate_screen_input_cmd_command_is_executed() {
    if (aggregate == null) {
      aggregate = new ScreenMapAggregate("screen-map-s22");
    }
    Map<String, String> fields = new LinkedHashMap<>();
    fields.put("USERID", "TLR001");
    fields.put("AMOUNT", "100.00");
    cmd = new ValidateScreenInputCmd(aggregate.id(), "SCR-DEPOSIT", fields);
    try {
      resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void a_input_validated_event_is_emitted() {
    assertNull(thrownException, "Should not throw exception");
    assertNotNull(resultingEvents, "Events should not be null");
    assertEquals(1, resultingEvents.size(), "Should emit one event");
    assertTrue(resultingEvents.get(0) instanceof InputValidatedEvent, "Event type mismatch");

    InputValidatedEvent event = (InputValidatedEvent) resultingEvents.get(0);
    assertEquals("input.validated", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertEquals("SCR-DEPOSIT", event.screenId());
    assertEquals(2, event.inputFields().size());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException, "Expected a domain error to be thrown");
    assertTrue(
      thrownException instanceof IllegalStateException
        || thrownException instanceof IllegalArgumentException,
      "Expected IllegalStateException or IllegalArgumentException, got " + thrownException.getClass());
  }
}
