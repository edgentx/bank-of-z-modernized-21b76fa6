package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMapAggregate aggregate;
  private ValidateScreenInputCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid ScreenMap aggregate")
  public void aValidScreenMapAggregate() {
    aggregate = new ScreenMapAggregate("screen-1");
    // Configure a simple field for validation purposes
    aggregate.configureField("accountNum", true, 10);
  }

  @Given("a valid screenId is provided")
  public void aValidScreenIdIsProvided() {
    // Handled in constructor or context setup
  }

  @Given("a valid inputFields is provided")
  public void aValidInputFieldsIsProvided() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("accountNum", "123456789"); // Valid length, mandatory field present
    this.cmd = new ValidateScreenInputCmd("screen-1", inputs);
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void theValidateScreenInputCmdCommandIsExecuted() {
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void aInputValidatedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
    assertEquals("input.validated", resultEvents.get(0).type());
    assertNull(thrownException);
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void aScreenMapAggregateThatViolatesMandatoryFields() {
    aggregate = new ScreenMapAggregate("screen-mandatory");
    aggregate.configureField("refId", true, 10);

    Map<String, String> inputs = new HashMap<>();
    // Missing 'refId'
    inputs.put("optionalField", "value");
    this.cmd = new ValidateScreenInputCmd("screen-mandatory", inputs);
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalArgumentException);
    assertTrue(thrownException.getMessage().contains("validation failed") || 
               thrownException.getMessage().contains("violated"));
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void aScreenMapAggregateThatViolatesFieldLengths() {
    aggregate = new ScreenMapAggregate("screen-length");
    aggregate.configureField("shortCode", true, 5);

    Map<String, String> inputs = new HashMap<>();
    inputs.put("shortCode", "123456"); // Length 6 > Max 5
    this.cmd = new ValidateScreenInputCmd("screen-length", inputs);
  }
}