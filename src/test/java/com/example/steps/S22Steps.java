package com.example.steps;

import com.example.domain.routing.model.InputValidatedEvent;
import com.example.domain.routing.model.ScreenMapAggregate;
import com.example.domain.routing.model.ValidateScreenInputCmd;
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
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in the 'When' step construction via context, or set up here implicitly.
        // We will construct the full command in the When step to ensure validity context.
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in the 'When' step.
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            Map<String, String> inputs = new HashMap<>();
            inputs.put("USER_ID", "ALICE");
            inputs.put("TRAN_CODE", "TX01");
            inputs.put("AMOUNT", "100.00");

            cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        // Setup for failure: missing mandatory fields logic in the When step
    }

    // Reusing the generic When step, but injecting bad data context requires specific Given/When chaining.
    // Since Cucumber 'When' matches by text, we differentiate scenarios by context or overloaded methods if allowed.
    // Here, the previous "When the ValidateScreenInputCmd command is executed" matches all.
    // To handle different data, we rely on the context established in Given.
    // However, the simple implementation above uses hardcoded valid data.
    // We will modify the steps to support the negative scenarios via state.

    private Map<String, String> currentInputs = new HashMap<>();

    @And("a valid inputFields is provided")
    public void setupValidFields() {
        currentInputs.put("USER_ID", "ALICE");
        currentInputs.put("TRAN_CODE", "TX01");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void executeCommand() {
        try {
            // Default to valid inputs if context hasn't set up specific bad ones
            if (currentInputs.isEmpty() && caughtException == null) {
                 setupValidFields();
            }
            cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", currentInputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Negative Scenario 1 Steps
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateViolatingMandatoryRules() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        currentInputs.clear();
        // Violation: Missing TRAN_CODE (mandatory)
        currentInputs.put("USER_ID", "BOB");
        // TRAN_CODE is missing
    }

    // Negative Scenario 2 Steps
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateViolatingLengthConstraints() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        currentInputs.clear();
        currentInputs.put("USER_ID", "ALICE");
        currentInputs.put("TRAN_CODE", "TX01");
        // Violation: AMOUNT max is 12
        currentInputs.put("AMOUNT", "1234567890123"); 
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(caughtException.getMessage().contains("Validation failed"), "Error message should indicate validation failure");
        assertNull(resultEvents, "No events should be emitted on failure");
    }
}
