package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Constants for testing
    private static final String VALID_SCREEN_ID = "LOGIN_SCREEN";
    private static final String VALID_DEVICE_TYPE = "DESKTOP";
    private static final String INVALID_MANDATORY_SCREEN_ID = null;
    // Exceeds BMS constraint (assumed 80 in aggregate)
    private static final String INVALID_LONG_SCREEN_ID = "A".repeat(81); 

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(id);
        // Initialize to pass internal state checks
        aggregate.initialize(VALID_SCREEN_ID);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup handled in the 'When' step for this specific data
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup handled in the 'When' step for this specific data
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), VALID_SCREEN_ID, VALID_DEVICE_TYPE);
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(id);
        aggregate.initialize(VALID_SCREEN_ID);
    }

    @When("the RenderScreenCmd command is executed with missing mandatory field")
    public void the_render_screen_cmd_command_is_executed_with_missing_field() {
        RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), INVALID_MANDATORY_SCREEN_ID, VALID_DEVICE_TYPE);
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(id);
        aggregate.initialize(VALID_SCREEN_ID);
    }

    @When("the RenderScreenCmd command is executed with invalid field length")
    public void the_render_screen_cmd_command_is_executed_with_invalid_length() {
        RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), INVALID_LONG_SCREEN_ID, VALID_DEVICE_TYPE);
        executeCommand(cmd);
    }

    private void executeCommand(RenderScreenCmd cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertTrue(event instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent sre = (ScreenRenderedEvent) event;
        assertEquals(VALID_SCREEN_ID, sre.screenId());
        assertEquals(VALID_DEVICE_TYPE, sre.deviceType());
        assertNotNull(sre.layout());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        
        // Check for specific exception types expected from Domain validation
        assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, got: " + capturedException.getClass().getSimpleName()
        );
        
        // Ensure no events were published on failure
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }
}
