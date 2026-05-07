package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd.
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private Exception caughtException;
    private Iterable<DomainEvent> resultingEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in the When step by constructing a valid command
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Defaults to valid data for the happy path or setup by specific scenario steps
            String sid = "SCRN01";
            String dev = "3270";
            cmd = new RenderScreenCmd(sid, dev);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultingEvents);
        assertTrue(resultingEvents.iterator().hasNext(), "Should contain at least one event");
        DomainEvent event = resultingEvents.iterator().next();
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Violation triggered by passing null/blank in the When step
        // We override the command logic in the next When block essentially, 
        // but Cucumber executes steps sequentially. 
        // We will handle the specific invalid data injection via a custom logic or assumption in 'When'
        // For simplicity in this structure, we will interpret the Given as setting up the context for a specific invalid command.
        // However, the "When" step above is generic. To properly handle the negative flow, we should technically
        // have a specific When or modify the command construction.
        // Let's assume the "When" is generic and we adjust the command fields based on the state.
        // But standard Cucumber implies specific steps for specific actions.
        // Let's assume the generic When is used, but we need to override the command.
        // Implementation detail: We will assume the default 'When' creates a VALID command.
        // For the negative scenarios, we need a specific 'When' that constructs an invalid command 
        // OR we modify the aggregate state?
        // Actually, the prompt implies using the generic When.
        // To make this work robustly, I will add a specific When for the negative cases below.
    }

    // Specific When for negative scenarios to ensure correct context
    @When("the RenderScreenCmd command is executed with invalid fields")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidFields() {
        try {
            // Missing screenId
            cmd = new RenderScreenCmd(null, "3270");
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid length fields")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidLengthFields() {
        try {
            // Screen ID > 8 chars
            cmd = new RenderScreenCmd("VERY_LONG_SCREEN_ID", "3270");
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }
}
