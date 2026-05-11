package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenInputValidatedEvent;
import com.example.domain.uinavigation.model.ValidateScreenInputCmd;
import com.example.domain.uinavigation.model.ScreenMap;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMap screenMap;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // ID is mandatory for construction
        screenMap = new ScreenMap("SM-DEFAULT");
        
        // Define a schema for testing purposes.
        // Field 'USER' is optional (not mandatory), max 10 chars.
        screenMap.configureField("USER", false, 10);
        // Field 'ACTION' is mandatory, max 6 chars (e.g. SUBMIT).
        screenMap.configureField("ACTION", true, 6);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateViolatingMandatoryFields() {
        screenMap = new ScreenMap("SM-MISSING");
        // Make 'REF' mandatory
        screenMap.configureField("REF", true, 20);
        // We will intentionally fail to provide 'REF' in the input step
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateViolatingFieldLengths() {
        screenMap = new ScreenMap("SM-LENGTH");
        // Define 'NOTES' with a strict legacy limit of 50 chars
        screenMap.configureField("NOTES", false, 50);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // No specific state needed, handled in command construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in command construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            Map<String, String> inputs;
            
            // Determine input context based on the setup
            if (screenMap.id().equals("SM-MISSING")) {
                // Scenario: Mandatory field missing
                inputs = new HashMap<>();
                inputs.put("OTHER", "data"); // Missing 'REF'
            } else if (screenMap.id().equals("SM-LENGTH")) {
                // Scenario: Field too long
                inputs = new HashMap<>();
                inputs.put("NOTES", "A".repeat(51)); // Exceeds 50
            } else {
                // Scenario: Success
                inputs = new HashMap<>();
                inputs.put("USER", "alice");
                inputs.put("ACTION", "VIEW");
            }

            cmd = new ValidateScreenInputCmd(screenMap.id(), "SCR-001", inputs);
            resultEvents = screenMap.execute(cmd);
            capturedException = null;
            
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", validatedEvent.type());
        assertEquals(screenMap.id(), validatedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        
        // Check message content to ensure correct invariant violation
        String msg = capturedException.getMessage();
        assertTrue(
            msg.contains("mandatory") || msg.contains("constraints"),
            "Error message should indicate the specific invariant violation. Got: " + msg
        );
    }
}
