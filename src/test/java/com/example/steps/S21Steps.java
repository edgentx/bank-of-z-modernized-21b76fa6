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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in the 'When' step construction for simplicity, or stored here if needed
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in the 'When' step construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid data for success scenario
        command = new RenderScreenCmd("LOGINSCR", "3270");
        try {
            resultEvents = aggregate.execute(command);
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
        assertNotNull(event.layout());
        assertEquals("LOGINSCR", event.layout().get("screenId"));
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
    }

    @When("the RenderScreenCmd command is executed with blank screenId")
    public void theRenderScreenCmdCommandIsExecutedWithBlankScreenId() {
        command = new RenderScreenCmd("   ", "3270"); // Violation: blank screenId
        try {
            aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("mandatory") || capturedException.getMessage().contains("constraint"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void theRenderScreenCmdCommandIsExecutedWithLongScreenId() {
        // Violation: screenId > 8 chars
        command = new RenderScreenCmd("LONG_SCREEN_NAME", "3270");
        try {
            aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
