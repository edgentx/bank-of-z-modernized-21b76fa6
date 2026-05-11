package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.InputValidatedEvent;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ValidateScreenInputCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMapAggregate aggregate;
  private ValidateScreenInputCmd cmd;
  private List<com.example.domain.shared.DomainEvent> result;
  private Exception caughtException;

  @Given("a valid ScreenMap aggregate")
  public void aValidScreenMapAggregate() {
    this.aggregate = new ScreenMapAggregate("SCREEN_001");
  }

  @Given("a valid screenId is provided")
  public void aValidScreenIdIsProvided() {
    // Handled in context setup, typically binding to cmd
  }

  @Given("a valid inputFields is provided")
  public void aValidInputFieldsIsProvided() {
    // Handled in context setup
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void theValidateScreenInputCmdCommandIsExecuted() {
    try {
      if (this.cmd == null) {
        // Default valid command if not set by specific Given scenarios
        Map<String, String> inputs = new HashMap<>();
        inputs.put("acct_num", "123456");
        inputs.put("amount", "100.00");

        Set<String> mandatory = new HashSet<>();
        mandatory.add("acct_num");

        Map<String, Integer> lengths = new HashMap<>();
        lengths.put("acct_num", 10);
        lengths.put("amount", 10);

        this.cmd = new ValidateScreenInputCmd("SCREEN_001", inputs, mandatory, lengths);
      }
      this.result = aggregate.execute(cmd);
    } catch (Exception e) {
      this.caughtException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void aInputValidatedEventIsEmitted() {
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertTrue(result.get(0) instanceof InputValidatedEvent);
    InputValidatedEvent event = (InputValidatedEvent) result.get(0);
    assertEquals("input.validated", event.type());
    assertEquals("SCREEN_001", event.aggregateId());
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
    this.aggregate = new ScreenMapAggregate("SCREEN_ERR_001");
    Map<String, String> inputs = new HashMap<>();
    // Missing 'acct_num' which is mandatory
    inputs.put("amount", "100.00");

    Set<String> mandatory = new HashSet<>();
    mandatory.add("acct_num");

    Map<String, Integer> lengths = new HashMap<>();
    lengths.put("acct_num", 10);

    this.cmd = new ValidateScreenInputCmd("SCREEN_ERR_001", inputs, mandatory, lengths);
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void aScreenMapAggregateThatViolatesFieldLengths() {
    this.aggregate = new ScreenMapAggregate("SCREEN_ERR_002");
    Map<String, String> inputs = new HashMap<>();
    inputs.put("acct_num", "12345678901"); // Length 11, max is 10

    Set<String> mandatory = new HashSet<>();

    Map<String, Integer> lengths = new HashMap<>();
    lengths.put("acct_num", 10);

    this.cmd = new ValidateScreenInputCmd("SCREEN_ERR_002", inputs, mandatory, lengths);
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalArgumentException);
    // Optional: Assert message content if needed, e.g.,
    // assertTrue(caughtException.getMessage().contains("mandatory"));
  }
}
