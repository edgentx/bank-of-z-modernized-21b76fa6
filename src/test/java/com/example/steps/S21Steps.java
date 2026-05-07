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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Step Definitions for Story S-21 (RenderScreenCmd).
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // ScreenId is typically set when the command is constructed in the 'When' step.
        // This step ensures the state for the next step.
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // DeviceType is set in the 'When' step.
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command construction for the positive flow
        if (cmd == null) {
            cmd = new RenderScreenCmd(
                    "screen-map-1",
                    "LOGIN_SCREEN",
                    "DESKTOP",
                    Map.of("ACCOUNT_ID", "123456789")
            );
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("screen-map-1", event.aggregateId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-bad-fields");
        // Command is constructed here to simulate the violation condition
        cmd = new RenderScreenCmd(
                "screen-map-bad-fields",
                "SUMMARY_SCREEN",
                "MOBILE",
                Map.of() // Missing mandatory ACCOUNT_ID
        );
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengths() {
        aggregate = new ScreenMapAggregate("screen-map-bad-lengths");
        // Account ID constraint is 12 chars.
        cmd = new RenderScreenCmd(
                "screen-map-bad-lengths",
                "SUMMARY_SCREEN",
                "DESKTOP",
                Map.of("ACCOUNT_ID", "1234567890123") // 13 chars
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
