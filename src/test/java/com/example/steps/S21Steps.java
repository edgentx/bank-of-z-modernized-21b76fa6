package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("sm-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // State stored for when the command is executed
        // This step assumes we are in the happy path or setup context
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // State stored for when the command is executed
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default command construction for Happy Path
        if (cmd == null) {
            cmd = new RenderScreenCmd("LOGIN_SCR", "3270");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertTrue("Expected ScreenRenderedEvent", resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("sm-1", event.aggregateId());
        assertEquals("LOGIN_SCR", event.screenId());
        assertEquals("3270", event.deviceType());
        assertNull("Expected no exception", caughtException);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("sm-2");
        // Scenario 2: Violate mandatory fields (null screenId)
        cmd = new RenderScreenCmd(null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("sm-3");
        // Scenario 3: Violate BMS length constraint (> 8 chars)
        cmd = new RenderScreenCmd("VERY_LONG_SCREEN_ID", "3270");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        assertTrue("Expected IllegalArgumentException", caughtException instanceof IllegalArgumentException);
    }
}
