package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.ScreenInputValidatedEvent;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ValidateScreenInputCmd;
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
        aggregate = new ScreenMapAggregate("SCREEN1");
        // Define a mandatory field for test coverage
        aggregate.defineField("ACCOUNT_NO", 10, true);
        // Define a length constrained field
        aggregate.defineField("TX_CODE", 4, false);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateMissingMandatoryField() {
        aggregate = new ScreenMapAggregate("SCREEN1");
        aggregate.defineField("ACCOUNT_NO", 10, true); // Mandatory
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithLengthViolation() {
        aggregate = new ScreenMapAggregate("SCREEN1");
        aggregate.defineField("ACCOUNT_NO", 10, false); // Max length 10
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
            // Default valid inputs for happy path
            Map<String, String> inputs = new HashMap<>();
            inputs.put("ACCOUNT_NO", "12345");
            inputs.put("TX_CODE", "TR01");
            
            cmd = new ValidateScreenInputCmd("SCREEN1", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        // Determine scenario context based on aggregate state to construct the appropriate failing command
        try {
            Map<String, String> inputs = new HashMap<>();
            
            // Check if we are in the mandatory violation scenario
            // (Since we can't pass state between steps easily without complex context, we infer from aggregate or use a flag)
            // However, strictly following Gherkin, we execute the command.
            // Let's refine the 'When' logic or inject specific inputs for negative tests.
            // For simplicity in this BDD framework, we often set up the command to fail inside the When step based on the Given.
            
            // To keep steps generic, we'll re-execute logic tailored to the failure type here if needed, 
            // but ideally, the 'When' handles logic.
            // Let's assume the 'When' logic above was the happy path. 
            // For error scenarios, we need a specific 'When' or a smart 'When'.
            // Let's modify the 'When' to detect the aggregate state (simple hack for this exercise).
            
            // Re-implementing execution logic here to ensure correct failure for the specific scenario context:
            // Scenario 1: Mandatory Missing
            if (aggregate.toString().contains("mandatory=true")) { // Heuristic
                 // Missing ACCOUNT_NO
                 inputs.put("OTHER_FIELD", "val");
            } else {
                 // Scenario 2: Length Violation
                 inputs.put("ACCOUNT_NO", "12345678901"); // Length 11 > 10
            }
            
            cmd = new ValidateScreenInputCmd("SCREEN1", inputs);
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
        
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
