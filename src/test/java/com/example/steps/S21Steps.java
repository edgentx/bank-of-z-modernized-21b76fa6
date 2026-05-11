package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Using a short ID to satisfy BMS length constraints by default
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "DESKTOP");
        } else {
            // Record helper to update existing cmd instance if needed in more complex flows
            // Since Record is immutable, we reassign in the When step usually, but here we assume standard flow.
        }
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Already set in previous step for simplicity in this scenario, or can be explicitly set here
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Execute with the specific command configured by the scenario context
            // For the 'valid' case, we default to the one created in 'aValidScreenIdIsProvided'
            RenderScreenCmd commandToExecute = cmd;
            if (commandToExecute == null) {
                 // This handles cases where Given might set up bad data before creating the cmd object properly
                 // But here we rely on the specific Given methods below for invalid cases.
                 commandToExecute = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "DESKTOP");
            }
            aggregate.execute(commandToExecute);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) events.get(0);
        assertEquals("screen.rendered", event.type());
    }

    // --- Rejection Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
        // Missing screenId
        cmd = new RenderScreenCmd("screen-map-invalid", null, "DESKTOP");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-long");
        // Screen ID > 30 chars
        String longScreenId = "THIS_IS_A_VERY_LONG_SCREEN_NAME_THAT_EXCEEDS_LIMITS";
        cmd = new RenderScreenCmd("screen-map-long", longScreenId, "MOBILE");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
