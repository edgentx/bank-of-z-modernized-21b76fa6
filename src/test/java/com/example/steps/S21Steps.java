package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class S21Steps {

    private ScreenMap screenMap;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        screenMap = new ScreenMap("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in the When step construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Handled in the When step construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default valid command data
        Command cmd = new RenderScreenCmd(
            "screen-123",
            DeviceType.DESKTOP,
            List.of(new RenderScreenCmd.FieldSpec("user_name", 20, true)),
            List.of(new RenderScreenCmd.ValidationRule("user_name", 20))
        );
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_All_mandatory_input_fields_must_be_validated_before_screen_submission() {
        screenMap = new ScreenMap("screen-violate-mandatory");
    }

    @When("the RenderScreenCmd command is executed with missing mandatory fields")
    public void the_RenderScreenCmd_command_is_executed_with_missing_mandatory_fields() {
        // Field 'secret' is mandatory but missing from input
        Command cmd = new RenderScreenCmd(
            "screen-violate-mandatory",
            DeviceType.DESKTOP,
            List.of(new RenderScreenCmd.FieldSpec("user_name", 20, true)), // user_name is present
            List.of(new RenderScreenCmd.ValidationRule("secret", 10))     // secret is required by validation but not provided
        );
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_Field_lengths_must_strictly_adhere_to_legacy_BMS_constraints_during_the_transition_period() {
        screenMap = new ScreenMap("screen-violate-length");
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_field_lengths() {
        // Field input length (50) exceeds BMS constraint (10)
        Command cmd = new RenderScreenCmd(
            "screen-violate-length",
            DeviceType.TN3270,
            List.of(new RenderScreenCmd.FieldSpec("long_field", 50, true)),
            List.of(new RenderScreenCmd.ValidationRule("long_field", 10))
        );
        executeCommand(cmd);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull("Expected no exception", caughtException);
        assertNotNull("Expected events to be generated", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertTrue("Expected ScreenRenderedEvent", resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        assertTrue("Expected IllegalArgumentException or IllegalStateException", 
                   caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }
}
