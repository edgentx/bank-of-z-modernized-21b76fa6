package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Givens ---

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // We will construct the command in the 'When' step or store parts here.
        // For simplicity, let's assume we build the command dynamically or use a builder pattern.
        // Since records are immutable, we'll hold state in context fields.
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // See above.
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("map-invalid-input");
        // We'll construct the invalid command in the When step to specify *which* field is missing.
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("map-invalid-bms");
    }

    // --- Whens ---

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // We need to determine which scenario we are in to construct the appropriate command.
            // We'll infer based on the aggregate ID set in the Given steps.
            // This is a slight simplification for the step definition implementation.

            if (aggregate.id().equals("map-1")) {
                // Valid Scenario
                cmd = new RenderScreenCmd("SCREEN-001", "3270", Map.of("field1", "value"), 80);
            } else if (aggregate.id().equals("map-invalid-input")) {
                // Missing mandatory field
                cmd = new RenderScreenCmd("", "3270", Map.of(), 80);
            } else if (aggregate.id().equals("map-invalid-bms")) {
                // Exceeds BMS limit
                String longValue = "a".repeat(81);
                cmd = new RenderScreenCmd("SCREEN-002", "3270", Map.of("longField", longValue), 80);
            } else {
                // Default fallback
                cmd = new RenderScreenCmd("SCREEN-000", "WEB", Map.of(), 80);
            }

            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertEquals("screen.rendered", event.type());

        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals(cmd.screenId(), renderedEvent.screenId());
        assertEquals(cmd.deviceType(), renderedEvent.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
                caughtException instanceof IllegalArgumentException ||
                        caughtException instanceof IllegalStateException,
                "Expected a domain error (IllegalStateException/IllegalArgumentException), but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
