package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd.
 * Uses in-memory aggregate instantiation.
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private String deviceType;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-123");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "ACCNTSUM"; // 8 chars, valid BMS
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "3270-TERMINAL";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            Command cmd = new RenderScreenCmd(aggregate.id(), this.screenId, this.deviceType);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(this.screenId, event.screenId());
        assertEquals(this.deviceType, event.deviceType());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInput() {
        aggregate = new ScreenMapAggregate("map-999");
        // We set the invalid data in the 'And' steps or here by making them null
        this.screenId = null; // Violation
        this.deviceType = "Desktop";
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("map-bms");
        // BMS Constraint: Screen ID usually 8 chars max.
        this.screenId = "VERYLONGSCREENID"; // Violation
        this.deviceType = "Desktop";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Depending on implementation, it could be IllegalArgumentException (domain logic) or a specific DomainError
        assertTrue(caughtException instanceof IllegalArgumentException, 
            "Expected IllegalArgumentException, got " + caughtException.getClass().getSimpleName());
    }

    // Specific violation helpers for the Given
    @And("a valid screenId is not provided")
    public void aValidScreenIdIsNotProvided() {
        this.screenId = "";
    }

    @And("a valid screenId exceeding BMS length is provided")
    public void aValidScreenIdExceedingBmsLengthIsProvided() {
        this.screenId = "OVERLENGTH";
    }
}
