package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private Exception capturedException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SM-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Stored implicitly when command is constructed, initialized to valid default
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Stored implicitly when command is constructed
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Default valid command construction for the success scenario
            if (command == null) {
                command = new RenderScreenCmd("SM-1", "LOGIN-01", "DESKTOP", Map.of("user", "testUser"));
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputs() {
        aggregate = new ScreenMapAggregate("SM-ERR-1");
        // Scenario 1: Empty inputs map
        command = new RenderScreenCmd("SM-ERR-1", "LOGIN-01", "DESKTOP", Map.of());
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("SM-ERR-2");
        // Scenario 2: Input value > 32 chars
        String longString = "a".repeat(33);
        command = new RenderScreenCmd("SM-ERR-2", "LOGIN-01", "3270", Map.of("description", longString));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
        assertTrue(capturedException.getMessage().contains("Validation failed"), "Error message should indicate validation failure");
    }
}
