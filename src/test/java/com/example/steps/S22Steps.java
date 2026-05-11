package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ValidateScreenInputCmd;
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
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCR001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in command construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Construct valid inputs
            Map<String, String> inputs = new HashMap<>();
            inputs.put("ACCOUNT_NUMBER", "123456789"); // Valid length < 10
            
            cmd = new ValidateScreenInputCmd("SCR001", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertEquals("input.validated", resultEvents.get(0).type());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    // Scenario 2: Missing Mandatory Fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("SCR001");
        // This sets up the context, the violation occurs in the 'When' step inputs
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecutedInvalidMandatory() {
        try {
            // Construct invalid inputs (missing mandatory ACCOUNT_NUMBER)
            Map<String, String> inputs = new HashMap<>();
            // inputs.put("ACCOUNT_NUMBER", ""); // Missing
            inputs.put("OPTIONAL_FIELD", "value");

            cmd = new ValidateScreenInputCmd("SCR001", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("Validation failed"));
        assertNull(resultEvents);
    }

    // Scenario 3: BMS Length Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("SCR001");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecutedInvalidBms() {
        try {
            // Construct invalid inputs (Field too long for BMS)
            Map<String, String> inputs = new HashMap<>();
            inputs.put("ACCOUNT_NUMBER", "123456789012345"); // Length > 10

            cmd = new ValidateScreenInputCmd("SCR001", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
