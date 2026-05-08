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
    private String screenId;
    private String deviceType;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "LOGIN01";
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "3270";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            Command cmd = new RenderScreenCmd("map-1", this.screenId, this.deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
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

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("map-2");
        this.screenId = ""; // Violation: blank screenId
        this.deviceType = "3270";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("map-3");
        // BMS Constraint assumed: screenId max 12 chars
        this.screenId = "VERY_LONG_SCREEN_ID"; // Violation: > 12 chars
        this.deviceType = "3270";
    }
}