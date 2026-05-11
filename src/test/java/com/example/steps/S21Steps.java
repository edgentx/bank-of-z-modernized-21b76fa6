package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd.
 * Uses in-memory aggregate instances to verify domain logic.
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("MAP-001");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Command construction is finalized in the 'When' step for flexibility,
        // but this step ensures valid defaults are in mind.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Command construction is finalized in the 'When' step.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command if not modified by specific 'Given violation' steps
        if (command == null) {
            command = new RenderScreenCmd("MAP-001", "LOGIN_SCR", "3270");
        }
        try {
            resultingEvents = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents, "Expected a list of events");
        assertEquals(1, resultingEvents.size(), "Expected exactly one event");
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent, "Expected ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("MAP-001", event.aggregateId());
        assertEquals("LOGIN_SCR", event.screenId());
        assertEquals("3270", event.deviceType());
        assertNotNull(event.occurredAt());
    }

    // --- Scenarios for Violations ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("MAP-ERR-01");
        // Setup command with missing/invalid fields
        // Test 1: null screenId
        this.command = new RenderScreenCmd("MAP-ERR-01", null, "WEB");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        this.aggregate = new ScreenMapAggregate("MAP-LEN-01");
        // BMS Legacy limit: assume 32 chars. This is 33.
        String longScreenId = "OVERLY_LONG_SCREEN_NAME_THAT_FAILS"; // Length 34
        this.command = new RenderScreenCmd("MAP-LEN-01", longScreenId, "3270");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException for domain error");
        assertNull(resultingEvents, "No events should be emitted when command is rejected");
    }
}
