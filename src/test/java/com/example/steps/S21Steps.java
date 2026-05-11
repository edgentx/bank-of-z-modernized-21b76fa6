package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup, typically combined with command execution
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup
    }

    // --- Scenario 1: Success ---

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default command construction for happy path
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("screen-map-123", "LOGIN_SCR", "3270", Map.of());
        }
        
        try {
            this.resultingEvents = this.aggregate.execute(this.cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(this.resultingEvents, "Events list should not be null");
        assertFalse(this.resultingEvents.isEmpty(), "Events list should not be empty");
        
        DomainEvent event = this.resultingEvents.get(0);
        assertTrue(event instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertEquals("screen.rendered", event.type());
    }

    // --- Scenario 2: Missing Fields ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("invalid-screen-map");
        this.cmd = new RenderScreenCmd("invalid-screen-map", null, "3270", Map.of()); // screenId is null
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.thrownException, "Expected an exception to be thrown");
        assertTrue(this.thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }

    // --- Scenario 3: Legacy Constraints ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyBMSConstraints() {
        this.aggregate = new ScreenMapAggregate("bms-violator");
        // Screen ID > 80 chars
        String tooLongScreenId = "A".repeat(100); 
        this.cmd = new RenderScreenCmd("bms-violator", tooLongScreenId, "3270", Map.of());
    }
}
