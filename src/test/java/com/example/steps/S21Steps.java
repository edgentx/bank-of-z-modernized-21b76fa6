package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
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
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingFields() {
        // The aggregate itself is valid, but we will construct a bad command
        aggregate = new ScreenMapAggregate("map-1");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithInvalidLengths() {
        // The aggregate itself is valid, but we will construct a command with fields that violate length constraints
        aggregate = new ScreenMapAggregate("map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Stored for use in the command construction later, or we construct the command here in a valid state
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Stored for use in the command construction later
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Construct command. In a real Cucumber test, these would likely come from scenario parameters or context.
            // For this structure, we check the 'Given' conditions to determine validity.
            // Based on scenario titles, we infer the command payload:
            // Scenario 1: Valid
            if (caughtException == null && "map-1".equals(aggregate.id()) && resultEvents == null) {
                cmd = new RenderScreenCmd("map-1", "LOGIN01", "3270");
            }
            // Scenario 2: Missing fields (Mandatory validation)
            else if (aggregate.id().equals("map-1")) {
                 cmd = new RenderScreenCmd("map-1", "", "3270"); // blank screenId
            }
            // Scenario 3: Length constraints
            else {
                cmd = new RenderScreenCmd("map-1", "TO_LONG_SCREEN_ID", "3270");
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("map-1", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Helper to reset state between scenarios if not handled by Cucumber automatically
    // In a real setup, you might inject a Scenario context.
}
