package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-21: RenderScreenCmd.
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-01");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Placeholder - context handled in When
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Placeholder - context handled in When
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("map-bad-01");
        // Command will be constructed with nulls/blank in the 'When' step
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("map-long-01");
        // Command will be constructed with a very long string in the 'When' step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Determine scenario based on aggregate ID (simple heuristic for Cucumber context)
        Command cmd;
        if (aggregate.id().equals("map-bad-01")) {
            // Scenario 2: Missing fields
            cmd = new RenderScreenCmd(aggregate.id(), "", "3270", Map.of());
        } else if (aggregate.id().equals("map-long-01")) {
            // Scenario 3: Field length violation (Limit is 80)
            String longId = "a".repeat(81);
            cmd = new RenderScreenCmd(aggregate.id(), longId, "3270", Map.of());
        } else {
            // Scenario 1: Success
            cmd = new RenderScreenCmd(aggregate.id(), "LOGIN_SCR_01", "3270", Map.of());
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", renderedEvent.type());
        assertEquals(aggregate.id(), renderedEvent.aggregateId());
        assertNotNull(renderedEvent.occurredAt());
        assertEquals("LOGIN_SCR_01", renderedEvent.screenId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Exception should have been thrown");
        assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof UnknownCommandException,
            "Exception should be a domain rule violation (IllegalArgumentException or similar)"
        );
    }
}
