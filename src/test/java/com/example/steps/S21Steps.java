package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<?> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Kept in step context for command construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Kept in step context for command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Default valid command data for the 'Success' scenario
            String sid = "MAIN_MENU";
            String dev = "3270";
            if (cmd == null) {
                cmd = new RenderScreenCmd("screen-map-1", sid, dev, null);
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("screen-map-1", event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        // Create a command with a null/blank screenId
        cmd = new RenderScreenCmd("screen-map-2", null, "3270", null);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should be captured");
        assertTrue(capturedException instanceof IllegalArgumentException, "Should be IllegalArgumentException");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyBMSConstraints() {
        aggregate = new ScreenMapAggregate("screen-map-3");
        // Create a command with a screenId > 32 chars (BMS constraint)
        String tooLongScreenId = "THIS_SCREEN_ID_IS_FAR_TOO_LONG_FOR_LEGACY_BMS";
        cmd = new RenderScreenCmd("screen-map-3", tooLongScreenId, "3270", null);
    }
}
