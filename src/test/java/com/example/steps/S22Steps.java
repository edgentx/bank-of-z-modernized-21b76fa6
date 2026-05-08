package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    // Test state
    private ScreenMapAggregate aggregate;
    private final Map<String, ScreenMapAggregate.FieldDefinition> fieldDefinitions = new HashMap<>();
    private final Map<String, String> inputFields = new HashMap<>();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Constants for testing
    private static final String SCREEN_MAP_ID = "SM-001";
    private static final String SCREEN_ID = "ACCT-INQ";

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate(SCREEN_MAP_ID);
        // Setup standard valid definitions
        fieldDefinitions.put("ACCOUNT_NUM", new ScreenMapAggregate.FieldDefinition(true, 10));
        fieldDefinitions.put("NAME", new ScreenMapAggregate.FieldDefinition(false, 30));
        aggregate.initializeDefinitions(fieldDefinitions);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate(SCREEN_MAP_ID);
        // Define 'ACCOUNT_NUM' as mandatory
        fieldDefinitions.put("ACCOUNT_NUM", new ScreenMapAggregate.FieldDefinition(true, 10));
        aggregate.initializeDefinitions(fieldDefinitions);
        
        // The violation (missing field) will be handled in the 'When' step by not adding it to inputFields
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate(SCREEN_MAP_ID);
        // Define 'ACCOUNT_NUM' with max length 10
        fieldDefinitions.put("ACCOUNT_NUM", new ScreenMapAggregate.FieldDefinition(true, 10));
        aggregate.initializeDefinitions(fieldDefinitions);
        
        // The violation (length > 10) will be handled in the 'When' step by adding a long value
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // This is implicit in how we construct the command, but we can log it if needed
        // No-op, effectively handled in construction
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        inputFields.put("ACCOUNT_NUM", "12345");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_MAP_ID, SCREEN_ID, inputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the ValidateScreenInputCmd command is executed without mandatory fields")
    public void the_validate_screen_input_cmd_command_is_executed_without_mandatory_fields() {
        // inputFields is deliberately left empty or missing the mandatory key
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_MAP_ID, SCREEN_ID, inputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the ValidateScreenInputCmd command is executed with exceeding length")
    public void the_validate_screen_input_cmd_command_is_executed_with_exceeding_length() {
        // Add a value that exceeds the BMS constraint of 10 defined in the Given step
        inputFields.put("ACCOUNT_NUM", "1234567890123"); // length 13
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_MAP_ID, SCREEN_ID, inputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
        
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", validatedEvent.type());
        assertEquals(SCREEN_MAP_ID, validatedEvent.aggregateId());
        assertEquals(SCREEN_ID, validatedEvent.screenId());
        assertEquals(inputFields, validatedEvent.inputFields());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be a domain error (IllegalStateException)");
    }

}
