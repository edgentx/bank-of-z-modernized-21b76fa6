package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in command construction
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Handled in command construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        // Default valid data for success scenario
        Map<String, String> inputs = Map.of(
            "mandatoryField", "12345",
            "optionalField", "optional"
        );
        cmd = new ValidateScreenInputCmd("SCRN01", inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent, "Event should be InputValidatedEvent");
    }

    // Scenario 2: Rejection - Mandatory Fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Setup command missing the mandatory field defined in the aggregate
        Map<String, String> inputs = Map.of("optionalField", "data"); // missing 'mandatoryField'
        cmd = new ValidateScreenInputCmd("SCRN01", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(thrownException.getMessage().contains("All mandatory input fields must be validated"), 
            "Exception message should mention mandatory fields");
    }

    // Scenario 3: Rejection - Field Lengths
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Setup command with a field that exceeds the defined length (10 chars)
        Map<String, String> inputs = Map.of(
            "mandatoryField", "12345", 
            "optionalField", "this_string_is_definitely_too_long_for_the_limit"
        );
        cmd = new ValidateScreenInputCmd("SCRN01", inputs);
    }

}