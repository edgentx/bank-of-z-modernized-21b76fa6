package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Scenario: Successfully execute RenderScreenCmd
    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // stored in builder, will be used in When
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // stored in builder, will be used in When
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            // Assuming default valid data if not set by specific violation steps
            if (cmd == null) {
                cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "3270_TERMINAL", "some-data");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("screen-map-1", event.aggregateId());
    }

    // Scenario: RenderScreenCmd rejected — Mandatory fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        // Violation: Null or Blank screenId
        cmd = new RenderScreenCmd("screen-map-2", null, "3270", "data");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }

    // Scenario: RenderScreenCmd rejected — BMS Length Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_length() {
        aggregate = new ScreenMapAggregate("screen-map-3");
        // Violation: Screen ID exceeds max length (Max is 80 in Aggregate)
        String longScreenId = "A".repeat(81); 
        cmd = new RenderScreenCmd("screen-map-3", longScreenId, "3270", "data");
    }
}
