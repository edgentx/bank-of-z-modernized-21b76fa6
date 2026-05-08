package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("MAP001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // ScreenId is part of the command, verified in execution
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // deviceType is part of the command, verified in execution
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            command = new RenderScreenCmd("MAP001", "3270");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("MAP001", event.aggregateId());
    }

    // Scenario 2: Mandatory Input Validation
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("MAP001");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted_InvalidInput() {
        try {
            // Violating mandatory fields: null screenId
            command = new RenderScreenCmd(null, "3270");
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("mandatory"));
    }

    // Scenario 3: Legacy BMS Field Length Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengths() {
        aggregate = new ScreenMapAggregate("MAP001");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted_LengthViolation() {
        try {
            // Violating length: screenId > 8 chars
            command = new RenderScreenCmd("VERY_LONG_MAP_NAME", "3270");
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }
}
