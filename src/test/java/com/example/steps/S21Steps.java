package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
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
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        cmd = new RenderScreenCmd("screen-map-1", "LOGIN01", "3270");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // cmd initialized in previous step
        assertNotNull(cmd.screenId());
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // cmd initialized in previous step
        assertNotNull(cmd.deviceType());
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN01", event.screenId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        cmd = new RenderScreenCmd("screen-map-2", "", "3270"); // blank screenId
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
        cmd = new RenderScreenCmd("screen-map-3", "VERY_LONG_SCREEN_NAME", "3270"); // > 8 chars
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}