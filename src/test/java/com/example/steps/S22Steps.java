package com.example.steps;

import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenInputValidatedEvent;
import com.example.domain.screen.model.ValidateScreenInputCmd;
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
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
        // Configure some fields for validation logic
        Map<String, ScreenMapAggregate.FieldDefinition> defs = new HashMap<>();
        defs.put("USER_ID", new ScreenMapAggregate.FieldDefinition(10, true));
        defs.put("TX_AMOUNT", new ScreenMapAggregate.FieldDefinition(12, true));
        defs.put("OPTIONAL_MEMO", new ScreenMapAggregate.FieldDefinition(50, false));
        aggregate.configureFields(defs);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction below
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in command construction below
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Build command with valid data by default if not set otherwise
            if (cmd == null) {
                Map<String, String> inputs = new HashMap<>();
                inputs.put("USER_ID", "12345");
                inputs.put("TX_AMOUNT", "100.00");
                cmd = new ValidateScreenInputCmd("SCREEN-001", inputs);
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
        Map<String, ScreenMapAggregate.FieldDefinition> defs = new HashMap<>();
        defs.put("USER_ID", new ScreenMapAggregate.FieldDefinition(10, true)); // Mandatory
        aggregate.configureFields(defs);

        // Prepare command that misses the mandatory field
        Map<String, String> inputs = new HashMap<>();
        // USER_ID is missing
        cmd = new ValidateScreenInputCmd("SCREEN-001", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengths() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
        Map<String, ScreenMapAggregate.FieldDefinition> defs = new HashMap<>();
        defs.put("USER_ID", new ScreenMapAggregate.FieldDefinition(5, true)); // Max length 5
        aggregate.configureFields(defs);

        // Prepare command that exceeds length
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "1234567890"); // Length 10, exceeds 5
        cmd = new ValidateScreenInputCmd("SCREEN-001", inputs);
    }
}