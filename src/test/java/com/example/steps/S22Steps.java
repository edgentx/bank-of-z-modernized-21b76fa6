package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ValidateScreenInputCmd;
import com.example.domain.uinavigation.model.InputValidatedEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup handled in command execution
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Context setup handled in command execution
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        // Default valid inputs for the happy path
        Command cmd = new ValidateScreenInputCmd("SCREEN-001", Map.of("USER_ID", "12345", "ACTION", "INQ"));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent, "Event should be InputValidatedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
        // Map of inputs missing 'USER_ID' which is mandatory for SCREEN-002 in this scenario context
        // (Simulating business logic where USER_ID is required)
        // This context is stored implicitly to be used by the next step
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
        // Context for length violation
    }

    // Specific When/Then mappings for the error scenarios to ensure correct context capture
    // We reuse the generic When or implement specific ones. Cucumber will match appropriately.
    // For clarity, we implement specific logic handlers if needed, but here we overload the When logic
    // by inspecting the aggregate state which we set up in the Given.

    @When("the ValidateScreenInputCmd command is executed with missing mandatory field")
    public void the_command_executed_missing_mandatory() {
        // Submit empty map to trigger missing field error
        Command cmd = new ValidateScreenInputCmd("SCREEN-002", Map.of());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNull(resultEvents, "No events should be emitted on error");
        assertNotNull(capturedException, "Exception should be captured");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be domain error");
    }

    @When("the ValidateScreenInputCmd command is executed with invalid field length")
    public void the_command_executed_invalid_length() {
        // Submit value exceeding length (e.g. > 10 for USER_ID)
        Command cmd = new ValidateScreenInputCmd("SCREEN-003", Map.of("USER_ID", "123456789012345"));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
