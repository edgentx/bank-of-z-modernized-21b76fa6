package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.Assert.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // "valid" in this context means the aggregate ID is present
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Valid ID: non-empty and within legacy constraints
        cmd = new RenderScreenCmd("screen-map-1", "LOGIN001", "3270");
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Already set in previous step for simplicity, or update here
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN001", "WEB");
        }
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertTrue("Expected ScreenRenderedEvent", resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(cmd.screenId(), event.screenId());
        assertNull("Should not have thrown an exception", caughtException);
    }

    // ---------- Scenarios for Rejection ----------

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        // Violation: Missing ScreenId
        cmd = new RenderScreenCmd("screen-map-2", "", "WEB");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyLengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
        // Violation: Screen ID > 8 chars (Legacy BMS constraint)
        cmd = new RenderScreenCmd("screen-map-3", "VERY_LONG_SCREEN_NAME", "3270");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        assertTrue("Expected IllegalArgumentException", caughtException instanceof IllegalArgumentException);
        assertTrue("Expected events to be empty on failure", resultEvents == null || resultEvents.isEmpty());
    }
}