package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SM-001");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // No-op, handled in When construction or context storage
        // For simplicity in this pattern, we construct command in @When
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // No-op
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command if not overridden by a violation scenario
        if (cmd == null) {
            cmd = new RenderScreenCmd("SM-001", "LOGIN_SCREEN", "3270", null);
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCREEN", event.screenId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    // --- Validation Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SM-INVALID");
        // Create command with blank mandatory fields
        cmd = new RenderScreenCmd("SM-INVALID", "", "3270", null);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("SM-BMS-FAIL");
        Map<String, String> badData = new HashMap<>();
        badData.put("address", "X".repeat(100)); // Exceeds hypothetical limit of 80
        cmd = new RenderScreenCmd("SM-BMS-FAIL", "PROFILE_SCREEN", "3270", badData);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(caughtException.getMessage().contains("length") 
                   || caughtException.getMessage().contains("mandatory")
                   || caughtException.getMessage().contains("required"), 
                   "Exception message should indicate the specific validation failure: " + caughtException.getMessage());
        assertNull(resultEvents, "No events should be produced on validation failure");
    }

    // --- Helper to reset state between scenarios if needed ---
    // Cucumber creates a new instance per scenario, so constructors handle reset.
}
