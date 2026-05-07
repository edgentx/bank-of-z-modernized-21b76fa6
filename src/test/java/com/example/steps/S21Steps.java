package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in When or setup, usually combined in command construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in When or setup
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "DESKTOP");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("LOGIN_SCREEN", event.screenId());
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-error-1");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void theRenderScreenCmdCommandIsExecutedWithMissingFields() {
        // Missing screenId (null)
        cmd = new RenderScreenCmd("screen-map-error-1", null, "MOBILE");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(thrownException.getMessage().contains("mandatory") || thrownException.getMessage().contains("required"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("screen-map-bms-1");
    }

    @When("the RenderScreenCmd command is executed with invalid lengths")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidLengths() {
        // Screen ID exceeds max length of 80
        String longId = "A".repeat(81);
        cmd = new RenderScreenCmd("screen-map-bms-1", longId, "DESKTOP");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }
}
