package com.example.steps;

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
        aggregate = new ScreenMapAggregate("map-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "SCRN01"; // Valid BMS length (8 chars)
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "3270"; // Valid BMS length
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType, "{}");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(screenId, event.screenId());
        assertEquals(deviceType, event.deviceType());
        assertTrue(aggregate.isRendered());
    }

    // Scenario 2: Mandatory Input Fields Validation
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("map-002");
        this.screenId = null; // Violating mandatory field
        this.deviceType = "3270";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("mandatory"));
    }

    // Scenario 3: Legacy BMS Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyBMSConstraints() {
        aggregate = new ScreenMapAggregate("map-003");
        // Screen ID max length is 8 based on aggregate logic. Let's use 9 to violate.
        this.screenId = "TOOLONGID"; // 9 chars
        this.deviceType = "3270";
    }
}
