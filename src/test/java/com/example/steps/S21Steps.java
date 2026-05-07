package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
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
        this.aggregate = new ScreenMapAggregate("SM-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Valid ID used in the When step
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Valid type used in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command
        if (cmd == null) {
            cmd = new RenderScreenCmd("SM-001", "LOGIN_SCR_01", "TN3270");
        }
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertEquals("screen.rendered", resultEvents.get(0).type());
        assertNull(caughtException, "No exception should have been thrown");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInput() {
        this.aggregate = new ScreenMapAggregate("SM-ERR-01");
        this.cmd = new RenderScreenCmd("SM-ERR-01", null, "TN3270"); // screenId is null
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("SM-ERR-02");
        // Create a screenId that exceeds BMS limit (e.g., 80 chars)
        String longScreenId = "A".repeat(81);
        this.cmd = new RenderScreenCmd("SM-ERR-02", longScreenId, "TN3270");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
        assertTrue(caughtException.getMessage().contains("required") || caughtException.getMessage().contains("violation"),
                "Exception message should indicate specific domain error");
    }
}