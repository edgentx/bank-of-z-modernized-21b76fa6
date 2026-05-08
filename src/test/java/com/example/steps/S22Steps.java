package com.example.steps;

import com.example.domain.ui.model.InputValidatedEvent;
import com.example.domain.ui.model.ScreenMapAggregate;
import com.example.domain.ui.model.ValidateScreenInputCmd;
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

    // Scenario: Successfully execute ValidateScreenInputCmd
    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("sm-001");
        // Setup a definition requiring a name (max 20) and account (max 10)
        aggregate.defineField("name", 20, true);
        aggregate.defineField("account", 10, true);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // ScreenId is set during command construction in the 'When' step
    }

    @Given("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Fields are set during command construction in the 'When' step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("name", "John Doe");
        inputs.put("account", "12345");

        cmd = new ValidateScreenInputCmd("sm-001", "LOGIN_SCR", inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        assertEquals("input.validated", resultEvents.get(0).type());
        assertTrue(aggregate.isValidated());
    }

    // Scenario: ValidateScreenInputCmd rejected — Mandatory Fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("sm-002");
        aggregate.defineField("mandatoryField", 10, true);
    }

    @When("the ValidateScreenInputCmd command is executed for mandatory check")
    public void the_validate_screen_input_cmd_command_is_executed_mandatory() {
        // Input fields missing the mandatory field
        Map<String, String> inputs = new HashMap<>();
        // Intentionally empty or missing 'mandatoryField'

        cmd = new ValidateScreenInputCmd("sm-002", "TEST_SCR", inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error for mandatory")
    public void the_command_is_rejected_with_a_domain_error_mandatory() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertTrue(caughtException.getMessage().contains("All mandatory input fields"));
    }

    // Scenario: ValidateScreenInputCmd rejected — Field Lengths
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_length() {
        aggregate = new ScreenMapAggregate("sm-003");
        aggregate.defineField("shortField", 5, false); // Max 5 chars
    }

    @When("the ValidateScreenInputCmd command is executed for length check")
    public void the_validate_screen_input_cmd_command_is_executed_length() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("shortField", "TOO_LONG_TEXT"); // > 5 chars

        cmd = new ValidateScreenInputCmd("sm-003", "TEST_SCR", inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error for length")
    public void the_command_is_rejected_with_a_domain_error_length() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertTrue(caughtException.getMessage().contains("Field lengths must strictly adhere"));
    }

    // Generic Step for Domain Error check
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
