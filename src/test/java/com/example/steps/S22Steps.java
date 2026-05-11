package com.example.steps;

import com.example.domain.screen.model.ScreenMap;
import com.example.domain.screen.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

  private ScreenMap aggregate;
  private ValidateScreenInputCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid ScreenMap aggregate")
  public void aValidScreenMapAggregate() {
    this.aggregate = new ScreenMap("screen-1");
    // Reset state between scenarios if necessary, though new instance is safer
  }

  @Given("a valid screenId is provided")
  public void aValidScreenIdIsProvided() {
    // Handled in construction of command later
  }

  @Given("a valid inputFields is provided")
  public void aValidInputFieldsIsProvided() {
    // Handled in construction of command later
  }

  @And("a valid inputFields is provided")
  public void aValidInputFieldsIsProvidedAnd() {
    // Handled in construction of command later
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void theValidateScreenInputCmdCommandIsExecuted() {
    try {
      // Default valid command if not set up by specific Given steps
      if (cmd == null) {
        cmd = new ValidateScreenInputCmd("screen-1", "SCRN_01", Map.of("ACC_NUM", "12345", "TX_AMT", "100.00"));
      }
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void aInputValidatedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertEquals("input.validated", resultEvents.get(0).type());
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void aScreenMapAggregateThatViolatesMandatoryFields() {
    aggregate = new ScreenMap("screen-mandatory-fail");
    // The default setup has 'ACC_NUM' as mandatory. We will provide an empty map or map without it.
    cmd = new ValidateScreenInputCmd("screen-mandatory-fail", "SCRN_01", Map.of());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalArgumentException);
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void aScreenMapAggregateThatViolatesBMSLengths() {
    aggregate = new ScreenMap("screen-length-fail");
    // Default ACC_NUM max length is 10. We send 12.
    cmd = new ValidateScreenInputCmd("screen-length-fail", "SCRN_01", Map.of("ACC_NUM", "123456789012", "TX_AMT", "100"));
  }
}