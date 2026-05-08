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
    private RenderScreenCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Using a valid BMS length screen ID (<= 7 chars)
        command = new RenderScreenCmd("ACCTSUM", "DESKTOP");
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in aValidScreenIdIsProvided for simplicity in this flow, or can be chained
        if (command == null) {
            command = new RenderScreenCmd("ACCTSUM", "MOBILE");
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        // Invalid: missing screenId
        command = new RenderScreenCmd(null, "DESKTOP");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
        // Invalid: screenId > 7 chars (BMS constraint)
        command = new RenderScreenCmd("LONGSCREENID", "DESKTOP");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("ACCTSUM", event.screenId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
