package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // We use the default valid command construction pattern here
        // If 'command' variable is not yet initialized, we initialize it with valid defaults
        if (this.command == null) {
            this.command = new RenderScreenCmd("LOGIN_SCREEN", "3270_TERMINAL", 24, 80);
        }
        // Note: Modifying records requires creating a new instance, handled in 'When'
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in combination with screenId
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // If command is null (e.g., specific violation scenario), create a default one to be mutated by specific 'Given' logic if needed, or rely on the specific setup below.
            // For this implementation, we assume the command object was constructed or defaulted in the Given steps.
            if (command == null) command = new RenderScreenCmd("TEST", "DEVICE", 10, 10);
            
            resultEvents = aggregate.execute(command);
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
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
        assertEquals(command.screenId(), event.screenId());
        assertEquals(command.deviceType(), event.deviceType());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
        // Setup command with invalid data (blank screenId)
        command = new RenderScreenCmd("", "3270", 24, 80);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-invalid-bms");
        // Max BMS length is 32. We use 33.
        String longScreenId = "VERY_LONG_SCREEN_ID_EXCEEDING_BMS_LIMIT"; 
        command = new RenderScreenCmd(longScreenId, "3270", 24, 80);
    }
}
