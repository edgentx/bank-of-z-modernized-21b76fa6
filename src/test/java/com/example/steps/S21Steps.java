package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        this.thrownException = null;
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup handled in the When step via cmd construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup handled in the When step via cmd construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(
                "LOGIN_SCR", 
                "DESKTOP_WEB", 
                Map.of("USER", "admin")
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCR", event.screenId());
        assertEquals("DESKTOP_WEB", event.deviceType());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputs() {
        this.aggregate = new ScreenMapAggregate("screen-map-2");
    }

    @When("the RenderScreenCmd command is executed with invalid inputs")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidInputs() {
        try {
            // Missing screenId
            RenderScreenCmd cmd = new RenderScreenCmd(
                null, // Invalid
                "DESKTOP_WEB", 
                Map.of()
            );
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengths() {
        this.aggregate = new ScreenMapAggregate("screen-map-3");
        // Setup specific BMS constraint for the test
        aggregate.setFieldConstraints(Map.of("ACCOUNT_NUM", 5)); // Very short for testing
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidFieldLengths() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(
                "ACCT_SUMMARY", 
                "3270_TERMINAL", 
                Map.of("ACCOUNT_NUM", "1234567890") // Length 10 > 5
            );
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents); // Ensure no events were committed
    }
}
