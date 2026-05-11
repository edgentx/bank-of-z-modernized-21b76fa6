package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: ScreenMap Aggregate.
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1 & 2 & 3 Setup
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-001");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // State stored in context, not directly applied until command execution
        // But we can prime a command object if we wanted. 
        // For simplicity, we construct the command in the 'When' step using valid defaults unless specified.
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Same as above
    }

    // Scenario 2: Mandatory Fields Violation
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("map-002");
    }

    // Scenario 3: Length Violation
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyBMSLengths() {
        aggregate = new ScreenMapAggregate("map-003");
    }

    // Execution
    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Default valid command
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), "LOGIN_SCR", "3270_TERMINAL");
            
            // Override based on specific scenario needs if we stored flags, 
            // but given the simple Gherkin, we'll assume the 'Violates' Givens set the aggregate state
            // or we manually override the command for negative tests.
            
            // Detecting intent based on aggregate ID isn't ideal, but fits a simple step impl
            if (aggregate.id().equals("map-002")) {
                // Scenario 2: Mandatory field violation
                cmd = new RenderScreenCmd(aggregate.id(), "", "3270_TERMINAL");
            } else if (aggregate.id().equals("map-003")) {
                // Scenario 3: Length violation (> 10 chars for screenId)
                cmd = new RenderScreenCmd(aggregate.id(), "VERY_LONG_SCREEN_NAME", "3270_TERMINAL");
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Then
    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
