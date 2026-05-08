package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.InputValidatedEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SM-001");
        // Define a valid BMS layout for the test
        Map<String, ScreenMapAggregate.FieldDefinition> fields = new HashMap<>();
        fields.put("ACCOUNT_NUM", new ScreenMapAggregate.FieldDefinition(10, true));
        fields.put("TRANS_CODE", new ScreenMapAggregate.FieldDefinition(3, true));
        fields.put("AMOUNT", new ScreenMapAggregate.FieldDefinition(12, false));
        aggregate.initializeScreen("SCRN-01", fields);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command creation below
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in command creation below
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Construct a valid command based on the aggregate setup above
            Map<String, String> inputs = new HashMap<>();
            inputs.put("ACCOUNT_NUM", "1234567890"); // fits length 10, mandatory
            inputs.put("TRANS_CODE", "201");       // fits length 3, mandatory

            command = new ValidateScreenInputCmd("SM-001", "SCRN-01", inputs);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof InputValidatedEvent, "Event type mismatch");
        
        InputValidatedEvent validatedEvent = (InputValidatedEvent) event;
        Assertions.assertEquals("input.validated", validatedEvent.type());
        Assertions.assertEquals("SM-001", validatedEvent.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SM-002");
        Map<String, ScreenMapAggregate.FieldDefinition> fields = new HashMap<>();
        fields.put("ACCOUNT_NUM", new ScreenMapAggregate.FieldDefinition(10, true)); // MANDATORY
        aggregate.initializeScreen("SCRN-02", fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedForMandatoryViolation() {
        try {
            // Missing ACCOUNT_NUM
            Map<String, String> inputs = new HashMap<>();
            command = new ValidateScreenInputCmd("SM-002", "SCRN-02", inputs);
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException);
        Assertions.assertTrue(caughtException.getMessage().contains("mandatory"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLengthConstraints() {
        aggregate = new ScreenMapAggregate("SM-003");
        Map<String, ScreenMapAggregate.FieldDefinition> fields = new HashMap<>();
        fields.put("ACCOUNT_NUM", new ScreenMapAggregate.FieldDefinition(5, true)); // Max length 5
        aggregate.initializeScreen("SCRN-03", fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedForLengthViolation() {
        try {
            Map<String, String> inputs = new HashMap<>();
            inputs.put("ACCOUNT_NUM", "1234567890"); // Length 10 > 5
            command = new ValidateScreenInputCmd("SM-003", "SCRN-03", inputs);
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }
}
