package com.example.steps;

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
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    // Scenario: Successfully execute RenderScreenCmd
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction below
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in command construction below
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Assuming valid state for successful scenario
            cmd = new RenderScreenCmd("SCREEN-001", "3270", true, true);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should produce one event");
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertEquals("screen.rendered", resultingEvents.get(0).type());
    }

    // Scenario: RenderScreenCmd rejected — Mandatory fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
    }

    @When("the RenderScreenCmd command is executed with invalid mandatory fields")
    public void theCommandIsExecutedWithInvalidMandatoryFields() {
        try {
            // Simulating invariant failure: mandatoryFieldsValid = false
            cmd = new RenderScreenCmd("SCREEN-002", "WEB", false, true);
            aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding mandatory fields")
    public void theCommandIsRejectedWithDomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("mandatory input fields"));
    }

    // Scenario: RenderScreenCmd rejected — Field lengths
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void theCommandIsExecutedWithInvalidFieldLengths() {
        try {
            // Simulating invariant failure: fieldLengthsValid = false
            cmd = new RenderScreenCmd("SCREEN-003", "3270", true, false);
            aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error regarding BMS constraints")
    public void theCommandIsRejectedWithDomainErrorBMS() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("BMS constraints"));
    }
}
