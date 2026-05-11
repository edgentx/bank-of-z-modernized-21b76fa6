package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> events;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Implicitly handled in the When step via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Implicitly handled in the When step via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default valid command
        RenderScreenCmd cmd = new RenderScreenCmd("screen-123", DeviceType.MOBILE);
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should have emitted one event");
        assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        ScreenRenderedEvent event = (ScreenRenderedEvent) events.get(0);
        assertEquals("screen-123", event.aggregateId());
        assertEquals("screen.rendered", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_RenderScreenCmd_command_is_executed_with_missing_fields() {
        // Simulating a missing screenId or invalid deviceType
        RenderScreenCmd cmd = new RenderScreenCmd(null, null);
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalArgumentException, "Should be IllegalArgumentException");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-long");
    }

    @When("the RenderScreenCmd command is executed with invalid lengths")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_lengths() {
        // Screen ID > 8 chars violates BMS constraint
        String longScreenId = "very-long-screen-id";
        RenderScreenCmd cmd = new RenderScreenCmd(longScreenId, DeviceType.TERMINAL_3270);
        executeCommand(cmd);
    }

    // Helper
    private void executeCommand(RenderScreenCmd cmd) {
        try {
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
