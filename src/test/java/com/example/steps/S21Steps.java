package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<?> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMandatoryViolations() {
        // Setup a valid aggregate, but we will send an invalid command in the 'When' step
        aggregate = new ScreenMapAggregate("map-invalid-mandatory");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithLengthViolations() {
        aggregate = new ScreenMapAggregate("map-invalid-length");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup, command is constructed in the When step
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup, command is constructed in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command for the happy path or if the scenario didn't specify invalid data
        theRenderScreenCmdCommandIsExecutedWith("SCRN01", "3270");
    }

    @When("the RenderScreenCmd command is executed with blank screenId")
    public void theRenderScreenCmdCommandIsExecutedWithBlankScreenId() {
        theRenderScreenCmdCommandIsExecutedWith("", "3270");
    }

    @When("the RenderScreenCmd command is executed with blank deviceType")
    public void theRenderScreenCmdCommandIsExecutedWithBlankDeviceType() {
        theRenderScreenCmdCommandIsExecutedWith("SCRN01", "");
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidLengths() {
        // Length > 8 violates BMS constraint
        theRenderScreenCmdCommandIsExecutedWith("VERY_LONG_SCREEN_ID", "3270");
    }

    private void theRenderScreenCmdCommandIsExecutedWith(String screenId, String deviceType) {
        try {
            Command cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertTrue(aggregate.isRendered());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}