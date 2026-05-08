package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
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

    // Helper to reset state per scenario
    private void reset() {
        aggregate = null;
        cmd = null;
        resultEvents = null;
        caughtException = null;
    }

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        reset();
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        // Define a standard field for this screen
        // Name: USER_ID, Length: 10, Mandatory: true, Strict: true
        aggregate.defineField("USER_ID", 10, true, false);
        aggregate.defineField("PASSWORD", 20, true, false);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled by the specific scenario setup or default values in 'When'
        // We assume the command constructed in 'When' uses the correct ID matching the aggregate
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Handled in the 'When' step by constructing the map with valid data
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        reset();
        aggregate = new ScreenMapAggregate("DATA_ENTRY_SCREEN");
        // Define a mandatory field
        aggregate.defineField("ACCOUNT_NUM", 12, true, false);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        reset();
        aggregate = new ScreenMapAggregate("BMS_LEGACY_SCREEN");
        // Define a field with strict length check
        aggregate.defineField("TRAN_CODE", 4, true, false);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> inputs = new HashMap<>();
        
        // Context-aware input generation based on aggregate state (simulating context)
        if (aggregate != null) {
             // Try to provide inputs that might be valid or invalid based on Gherkin context.
             // Since Gherkin doesn't pass data strings here, we infer from the scenario name/state.
             // We assume the step definitions drive the specific valid/invalid cases.
             
             // Scenario 1: Valid
             if (aggregate.id().equals("LOGIN_SCREEN")) {
                 inputs.put("USER_ID", "valid_user"); // length 10 < 10
                 inputs.put("PASSWORD", "secret");
                 cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
             }
             // Scenario 2: Missing Mandatory
             else if (aggregate.id().equals("DATA_ENTRY_SCREEN")) {
                 inputs.put("ACCOUNT_NUM", ""); // Empty mandatory field
                 cmd = new ValidateScreenInputCmd("DATA_ENTRY_SCREEN", inputs);
             }
             // Scenario 3: Length Violation
             else if (aggregate.id().equals("BMS_LEGACY_SCREEN")) {
                 inputs.put("TRAN_CODE", "TOOLONGCODE"); // length 11 > 4
                 cmd = new ValidateScreenInputCmd("BMS_LEGACY_SCREEN", inputs);
             }
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent, "Event should be InputValidatedEvent");
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
