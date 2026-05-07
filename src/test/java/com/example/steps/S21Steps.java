package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String givenScreenId;
    private String givenDeviceType;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // ID matches the command ID in the success scenario
        aggregate = new ScreenMapAggregate("SM-001");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SM-ERROR-01");
        // We trigger the violation by setting empty/nulls in the 'And' steps below
        this.givenScreenId = null; // Intentionally null for violation
        this.givenDeviceType = "";
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("SM-BMS-01");
        this.givenScreenId = "TO_LONG_SCREEN_ID_VIOLATION";
        this.givenDeviceType = "3270";
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.givenScreenId = "ACC001"; // Within 8 chars
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.givenDeviceType = "DESKTOP";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        RenderScreenCmd cmd = new RenderScreenCmd(
            aggregate.id(),
            this.givenScreenId,
            this.givenDeviceType,
            "Standard Layout JSON"
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event must be ScreenRenderedEvent");

        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", renderedEvent.type());
        assertEquals(aggregate.id(), renderedEvent.aggregateId());
        assertEquals(this.givenScreenId, renderedEvent.screenId());
        assertEquals(this.givenDeviceType, renderedEvent.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
    }
}
