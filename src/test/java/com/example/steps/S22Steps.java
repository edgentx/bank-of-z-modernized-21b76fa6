package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCREEN1");
        // Setup: One mandatory field, one optional, both with BMS length constraints
        aggregate.configureField("ACCOUNT_NUM", true, 10);
        aggregate.configureField("TRANS_CODE", false, 3);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in construct of command in 'When'
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Valid inputs provided in 'When'
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "12345");
        inputs.put("TRANS_CODE", "001");
        
        command = new ValidateScreenInputCmd("SCREEN1", inputs);
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    // --- Scenario 2: Missing Mandatory ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCREEN2");
        aggregate.configureField("REF_NUM", true, 12); // Mandatory
        aggregate.configureField("NOTE", false, 50);
        
        // Input will lack REF_NUM in the When step below
    }

    @When("the ValidateScreenInputCmd command is executed for mandatory check")
    public void the_command_is_executed_for_mandatory_check() {
        Map<String, String> inputs = new HashMap<>(); 
        // Missing REF_NUM
        inputs.put("NOTE", "Some note");
        
        command = new ValidateScreenInputCmd("SCREEN2", inputs);
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    // --- Scenario 3: BMS Length Violation ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMapAggregate("SCREEN3");
        aggregate.configureField("SHORT_FIELD", true, 5);
    }

    @When("the ValidateScreenInputCmd command is executed for length check")
    public void the_command_is_executed_for_length_check() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("SHORT_FIELD", "TOOLONGTEXT"); // Length 11 > 5
        
        command = new ValidateScreenInputCmd("SCREEN3", inputs);
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    // Shared Then for rejection
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        // Verify message contains context (optional but good for BDD)
        assertTrue(caughtException.getMessage().length() > 0);
    }
}
