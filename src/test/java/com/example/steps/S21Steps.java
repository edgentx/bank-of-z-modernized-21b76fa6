package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private String deviceType;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("test-screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "LOGIN001"; // Valid: within length
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "DESKTOP"; // Valid: within length
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithViolatingMandatoryFields() {
        aggregate = new ScreenMapAggregate("test-screen-map-2");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithViolatingBmsConstraints() {
        aggregate = new ScreenMapAggregate("test-screen-map-3");
        // We set these values in the When step to trigger specific violation logic
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @When("the RenderScreenCmd command is executed with null screenId")
    public void theRenderScreenCmdCommandIsExecutedWithNullScreenId() {
        this.screenId = null;
        this.deviceType = "DESKTOP";
        theRenderScreenCmdCommandIsExecuted();
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void theRenderScreenCmdCommandIsExecutedWithLongScreenId() {
        this.screenId = "THIS-IS-A-VERY-LONG-SCREEN-ID"; // > 8 chars
        this.deviceType = "DESKTOP";
        theRenderScreenCmdCommandIsExecuted();
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof ScreenRenderedEvent);
        ScreenRenderedEvent rendered = (ScreenRenderedEvent) event;
        Assertions.assertEquals("screen.rendered", rendered.type());
        Assertions.assertEquals(screenId, rendered.screenId());
        Assertions.assertEquals(deviceType, rendered.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalArgumentException for validation errors
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Specific wiring for the scenario descriptions
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void setupMandatoryViolation() {
        aScreenMapAggregateWithViolatingMandatoryFields();
        theRenderScreenCmdCommandIsExecutedWithNullScreenId();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void setupBmsViolation() {
        aScreenMapAggregateWithViolatingBmsConstraints();
        theRenderScreenCmdCommandIsExecutedWithLongScreenId();
    }
}
