package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String inputScreenId;
    private String inputDeviceType;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
        caughtException = null;
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.inputScreenId = "SCRN01";
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.inputDeviceType = "3270";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Using the input fields populated in the Given steps
            RenderScreenCmd cmd = new RenderScreenCmd(inputScreenId, inputDeviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent renderedEvent = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", renderedEvent.type());
        assertEquals("SCRN01", renderedEvent.aggregateId());
        assertEquals("3270", renderedEvent.deviceType());
    }

    // Scenario 2: Validation - Mandatory fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Setup invalid data for mandatory fields
        this.inputScreenId = null; // Violation
        this.inputDeviceType = "TABLET";
        caughtException = null;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
        assertTrue(caughtException.getMessage().contains("required"), "Error message should indicate missing required field");
    }

    // Scenario 3: Validation - BMS Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Setup valid mandatory fields but invalid length for BMS
        // BMS Map names are typically short (e.g. 7 chars). Let's use 8.
        this.inputScreenId = "LONGMAP1"; // Length 8 (Constraint is 7)
        this.inputDeviceType = "3270";
        caughtException = null;
    }

}