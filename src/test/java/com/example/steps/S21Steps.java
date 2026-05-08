package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private String deviceType;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("map-01");
        this.caughtException = null;
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        this.screenId = "LOGIN_SCR_01"; // Valid BMS length
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        this.deviceType = "3270";
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("map-bad");
        // Simulate missing fields by setting them to null/blank in the context of the 'When' step logic
        // but typically here we set up the state that would cause the failure, or just the command context.
        // For this pattern, we set the context variables to invalid values.
        this.screenId = null; // Violation
        this.deviceType = "3270";
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_field_lengths() {
        this.aggregate = new ScreenMapAggregate("map-long");
        // Create a screen ID that is definitely longer than 80 chars
        this.screenId = "SCREEN_ID_THAT_IS_EXCESSIVELY_LONG_TO_VIOLATE_LEGACY_BMS_BUFFER_CONSTRAINTS_LIMIT_EIGHTY_CHARS";
        this.deviceType = "WEB";
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(deviceType, event.deviceType());
        assertNotNull(event.layoutJson());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException (Domain Error)");
    }
}