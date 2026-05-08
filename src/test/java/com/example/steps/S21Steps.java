package com.example.steps;

import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd.Builder cmdBuilder;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
        cmdBuilder = new RenderScreenCmd.Builder("screen-1", DeviceType.WEB);
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Default builder has valid ID
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Default builder has valid type
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            Command cmd = cmdBuilder.build();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen-1", event.aggregateId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-2");
        // Violation: null screenId handled by Builder enforcement or passed null if possible
        // Here we simulate a command with invalid internal state if builder allowed, or we test the command validation
        // Assuming the builder enforces basic non-null, we test an empty screenId
        cmdBuilder = new RenderScreenCmd.Builder("", DeviceType.WEB);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_BMS_constraints() {
        aggregate = new ScreenMapAggregate("screen-3");
        // Legacy BMS constraint: screen ID max 8 chars
        String longId = "very-long-screen-id-123";
        cmdBuilder = new RenderScreenCmd.Builder(longId, DeviceType.WEB);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected exception but command succeeded");
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Enums for test context
    public enum DeviceType { WEB, MOBILE, TSO }
}
