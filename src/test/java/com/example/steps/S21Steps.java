package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // We'll construct the command in the When step, 
        // but this step ensures the context is ready.
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context setup
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_with_violation_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
        // Define command with missing fields in When step
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_with_violation_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-map-long");
        // Define command with long fields in When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default command for valid case (if not set by other steps)
        // Note: In a real framework, we might inject this via a scenario context.
        if (cmd == null) {
             // Default valid command
            cmd = new RenderScreenCmd("SCRN01", "3270");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Specific When steps to define invalid commands based on the Givens above
    @When("the command with missing fields is executed")
    public void the_command_with_missing_fields_is_executed() {
        cmd = new RenderScreenCmd(null, "3270"); // Violation: null screenId
        the_RenderScreenCmd_command_is_executed();
    }

    @When("the command with invalid length fields is executed")
    public void the_command_with_invalid_length_fields_is_executed() {
        cmd = new RenderScreenCmd("WAY_TOO_LONG_SCREEN_ID", "3270"); // Violation: length > 10
        the_RenderScreenCmd_command_is_executed();
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
        assertNull(resultEvents, "No events should be emitted on failure");
    }
}
