package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
        aggregate = new ScreenMapAggregate("SM-TEST-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Defer command creation to When step to allow modification by violation steps
        if (cmd == null) cmd = new RenderScreenCmd("SM-TEST-001", "ACCTSUM1", "3270");
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        if (cmd == null) cmd = new RenderScreenCmd("SM-TEST-001", "ACCTSUM1", "3270");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SM-TEST-002");
        // Violation: null screenId
        cmd = new RenderScreenCmd("SM-TEST-002", null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyFieldLengths() {
        aggregate = new ScreenMapAggregate("SM-TEST-003");
        // Violation: screenId > 8 chars (Legacy BMS Map Set naming convention)
        cmd = new RenderScreenCmd("SM-TEST-003", "LONG_SCREEN_NAME", "3270");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // If we haven't constructed a specific command yet (valid case), build it now
            if (cmd == null) {
                cmd = new RenderScreenCmd(aggregate.id(), "ACCTSUM1", "3270");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(cmd.screenId(), event.screenId());
        assertEquals(cmd.deviceType(), event.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Exception should be a domain rule violation (IllegalArgumentException or IllegalStateException)"
        );
    }
}
