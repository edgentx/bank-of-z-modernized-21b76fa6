package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("map-1");
        this.capturedException = null;
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Will be used to construct valid command in When
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Will be used to construct valid command in When
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid values for the "Success" scenario or if not set by violation Given
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("map-1", "ACCTSUM", "3270");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected success but got error: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("ACCTSUM", event.screenId());
    }

    // --- Violations ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        this.aggregate = new ScreenMapAggregate("map-2");
        // Create a command with null/blank mandatory fields
        this.cmd = new RenderScreenCmd("map-2", null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("map-3");
        // Create a command where screenId exceeds legacy BMS constraints (e.g., > 30 chars)
        this.cmd = new RenderScreenCmd("map-3", "VERY_LONG_SCREEN_NAME_THAT_VIOLATES_BMS_LIMIT", "3270");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected domain error (IllegalArgumentException) but command succeeded");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }
}