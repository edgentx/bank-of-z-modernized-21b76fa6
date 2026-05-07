package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // ScreenId is set in the command construction, logic handled in 'When' or specific setup
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // DeviceType is set in the command construction, logic handled in 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // defaults for happy path
        String screenId = "SCRN01"; // valid length
        String deviceType = "DESKTOP";
        if (cmd == null) {
             cmd = new RenderScreenCmd("map-1", screenId, deviceType, null);
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("SCRN01", event.screenId());
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("map-err-1");
        // Setting up a command with null/blank data for the next step
        cmd = new RenderScreenCmd("map-err-1", null, "DESKTOP", null);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("map-err-2");
        // ScreenId max length is 10
        cmd = new RenderScreenCmd("map-err-2", "VERY_LONG_SCREEN_ID", "DESKTOP", null);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Depending on validation, could be IllegalArgumentException or specific Domain Error
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Specific overload for BMS violation scenario if needed contextually
    @When("the RenderScreenCmd command is executed with invalid constraints")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidConstraints() {
        try {
            if (cmd == null) {
                // Fallback if step definition order is ambiguous
                cmd = new RenderScreenCmd("map-err-2", "VERY_LONG_SCREEN_ID", "DESKTOP", null);
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
