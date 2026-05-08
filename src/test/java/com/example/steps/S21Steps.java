package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd.RenderScreenCmdBuilder cmdBuilder;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Helper to initialize the builder for positive cases
    private RenderScreenCmd.RenderScreenCmdBuilder createValidCmdBuilder() {
        return new RenderScreenCmd(
            "LOGIN001", // Valid 8-char ID
            "3270"
        );
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("sm-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Used in the positive scenario. We don't store the command, just the intent.
        // The command is constructed in the When block using valid defaults.
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Same as above.
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Default valid command for the happy path
            RenderScreenCmd cmd = new RenderScreenCmd("LOGIN001", "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertEquals("screen.rendered", event.type());
        assertEquals("sm-1", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("sm-2");
        // We will pass null/blank in the When step directly for this violation context
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("sm-3");
        // We will pass a long string in the When step directly
    }

    // Reusing the When method, but we'll handle the specific cases by setting up state or overwriting the command construction logic.
    // Since Cucumber steps don't support parameters in this context, we can add specific When methods or inspect the context.
    // However, standard BDD implies we execute the command and check the result.
    // To distinguish, I will add specific When/Then methods for the negative flows to be explicit, or use parameters.
    // Given the strict prompt, I will reuse the When method and assume the specific "Given" sets a flag or state that the When uses.
    // But simpler approach: I'll add specific When steps for the negative flows.

    @When("the RenderScreenCmd command is executed with invalid mandatory fields")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidMandatoryFields() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(null, "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with BMS length violations")
    public void theRenderScreenCmdCommandIsExecutedWithBMSLengthViolations() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("THIS_SCREEN_ID_IS_TOO_LONG", "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalArgumentException, "Should be an IllegalArgumentException (Domain Error)");
        assertTrue(caughtException.getMessage().contains("mandatory") || caughtException.getMessage().contains("Field length violation"));
    }
}