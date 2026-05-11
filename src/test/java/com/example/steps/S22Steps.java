package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMapAggregate aggregate;
  private Map<String, String> inputFields;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid ScreenMap aggregate")
  public void aValidScreenMapAggregate() {
    this.aggregate = new ScreenMapAggregate("SCREEN_001");
    // Setup valid definition for context
    aggregate.defineField("ACCOUNT_NUM", 10, true);
    aggregate.defineField("TX_AMOUNT", 12, false);
  }

  @Given("a valid screenId is provided")
  public void aValidScreenIdIsProvided() {
    // Handled by the command construction in the 'When' step
    // We assume the command targets the aggregate initialized above.
  }

  @Given("a valid inputFields is provided")
  public void aValidInputFieldsIsProvided() {
    this.inputFields = new HashMap<>();
    this.inputFields.put("ACCOUNT_NUM", "1234567890");
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void theValidateScreenInputCmdCommandIsExecuted() {
    try {
      ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCREEN_001", this.inputFields);
      this.resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      this.capturedException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void aInputValidatedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertEquals("input.validated", resultEvents.get(0).type());
    assertNull(capturedException);
  }

  // Scenario 2
  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void aScreenMapAggregateThatViolatesMandatoryFields() {
    this.aggregate = new ScreenMapAggregate("SCREEN_002");
    aggregate.defineField("REF_ID", 10, true); // Mandatory
  }

  // Reuse existing Given/When logic implies we just need to set the bad state
  @Given("a valid inputFields is provided") // Overriding behavior conceptually for the negative case
  public void inputFieldsMissingMandatory() {
    this.inputFields = new HashMap<>();
    // Intentionally leave REF_ID empty or missing to trigger violation
    // this.inputFields.put("REF_ID", ""); 
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalArgumentException);
    assertTrue(capturedException.getMessage().contains("Validation failed"));
  }

  // Scenario 3
  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void aScreenMapAggregateThatViolatesLengthConstraints() {
    this.aggregate = new ScreenMapAggregate("SCREEN_003");
    aggregate.defineField("SHORT_CODE", 5, false);
  }

  @Given("a valid inputFields is provided")
  public void inputFieldsExceedingLength() {
    this.inputFields = new HashMap<>();
    this.inputFields.put("SHORT_CODE", "123456"); // Length 6 > Max 5
  }
}
