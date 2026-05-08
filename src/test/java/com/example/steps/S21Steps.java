package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.ui.model.RenderScreenCmd;
import com.example.domain.ui.model.ScreenMapAggregate;
import com.example.domain.ui.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private Exception capturedException;
    private ScreenRenderedEvent lastEvent;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("LOGIN01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in setup or step combination, typically implies using a valid ID in the context
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in setup or step combination
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid execution
        if (cmd == null) {
            cmd = new RenderScreenCmd("LOGIN01", "3270");
        }
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (ScreenRenderedEvent) events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(lastEvent, "Expected a ScreenRenderedEvent to be emitted");
        assertEquals("screen.rendered", lastEvent.type());
        assertEquals("LOGIN01", lastEvent.aggregateId());
        assertEquals("3270", lastEvent.deviceType());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("TEST01");
        // Scenario 1: Null or Blank ScreenId
        cmd = new RenderScreenCmd(null, "WEB");
        // Could also be: cmd = new RenderScreenCmd("  ", "WEB");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("LONGSCREENNAME"); // > 8 chars
        cmd = new RenderScreenCmd("LONGSCREENNAME", "3270");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception (domain error) to be thrown");
        assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, but got: " + capturedException.getClass().getSimpleName()
        );
    }
}