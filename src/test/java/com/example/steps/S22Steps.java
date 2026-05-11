package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class S22Steps {

  private ScreenMapAggregate aggregate;
  private Map<String, String> inputFields;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_ScreenMap_aggregate() {
    aggregate = new ScreenMapAggregate("screen-123");
  }

  @Given("a valid screenId is provided")
  public void a_valid_screenId_is_provided() {
    // Screen ID is implicitly handled by the aggregate ID in this design, 
    // or passed into the command. For this step, we assume the command
    // will receive a matching screenId.
  }

  @Given("a valid inputFields is provided")
  public void a_valid_inputFields_is_provided() {
    // Setup the aggregate to expect specific fields (simulating a screen definition)
    aggregate.defineField("ACCOUNT_NUM", 10);
    aggregate.defineField("TRANSACTION_AMT", 12);

    // Provide valid inputs
    inputFields = new HashMap<>();
    inputFields.put("ACCOUNT_NUM", "1234567890");
    inputFields.put("TRANSACTION_AMT", "100.00");
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-missing-field");
    aggregate.defineField("REQ_FIELD_1", 5);
    aggregate.defineField("REQ_FIELD_2", 5);

    inputFields = new HashMap<>();
    // Missing REQ_FIELD_2
    inputFields.put("REQ_FIELD_1", "ABCDE");
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_ScreenMap_aggregate_that_violates_field_lengths() {
    aggregate = new ScreenMapAggregate("screen-length-violation");
    aggregate.defineField("SHORT_FIELD", 5);

    inputFields = new HashMap<>();
    // Input is too long
    inputFields.put("SHORT_FIELD", "1234567890");
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void the_ValidateScreenInputCmd_command_is_executed() {
    try {
      ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("screen-id", inputFields);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void a_input_validated_event_is_emitted() {
    assertNotNull(resultEvents, "Events list should not be null");
    assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
    assertTrue(resultEvents.get(0) instanceof InputValidatedEvent, "Event should be InputValidatedEvent");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "An exception should have been thrown");
    // Depending on whether we wrap business exceptions in a DomainError or just use RuntimeException/IllegalArgumentException
    assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException for domain rule violation");
  }
}
