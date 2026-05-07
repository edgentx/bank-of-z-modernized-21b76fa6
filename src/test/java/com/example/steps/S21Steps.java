package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private final String screenMapId = "map-123";

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate(screenMapId);
        assertNotNull(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // State setup handled in When step via command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // State setup handled in When step via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Defaults to valid values for the 'Successfully execute' scenario
            RenderScreenCmd cmd = new RenderScreenCmd(screenMapId, "scr-01", "mobile");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid screenId")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidScreenId() {
        try {
            // blank screenId
            RenderScreenCmd cmd = new RenderScreenCmd(screenMapId, "   ", "mobile");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidFieldLengths() {
        try {
            // screenId > 10 chars
            RenderScreenCmd cmd = new RenderScreenCmd(screenMapId, "very-long-screen-id", "mobile");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have uncommitted events");
        assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown exception");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
    }
}