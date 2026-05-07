package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMap;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd
 */
public class S21Steps {

    private ScreenMap aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String violationType; // "MANDATORY" or "CONSTRAINTS"

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMap("screen-map-123");
        this.violationType = null;
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Context setup, usually combined in the When step or via a context object
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Context setup
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("screen-map-invalid");
        this.violationType = "MANDATORY";
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_length_constraints() {
        aggregate = new ScreenMap("screen-map-long");
        this.violationType = "CONSTRAINTS";
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        String screenId;
        String deviceType;

        if ("MANDATORY".equals(violationType)) {
            screenId = ""; // Blank ID
            deviceType = "DESKTOP";
        } else if ("CONSTRAINTS".equals(violationType)) {
            screenId = "THIS-IS-A-Very-LONG-ID-THAT-EXCEEDS-BMS";
            deviceType = "DESKTOP";
        } else {
            // Valid case
            screenId = "LOGIN01";
            deviceType = "DESKTOP";
        }

        try {
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (IllegalArgumentException e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should produce exactly one event");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertNotNull(event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an IllegalArgumentException to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception must be IllegalArgumentException");
    }
}
