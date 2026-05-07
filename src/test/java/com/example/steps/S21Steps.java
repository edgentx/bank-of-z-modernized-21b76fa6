package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("sm-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // cmd construction happens in the 'When' or via specific state setup
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // cmd construction happens in the 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command if not overridden by violation scenarios
        if (cmd == null) {
            cmd = new RenderScreenCmd("sm-1", "LOGIN01", "DESKTOP");
        }
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertNotNull(event.layoutData());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("sm-bad-mandatory");
        // We simulate the violation by preparing an invalid command to be executed in the next step
        // Scenario A: Missing screenId
        cmd = new RenderScreenCmd("sm-bad-mandatory", null, "DESKTOP");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("sm-bad-bms");
        // We simulate the violation by preparing a command that exceeds legacy length constraints
        // Legacy constraint: max 8 chars for screenId
        cmd = new RenderScreenCmd("sm-bad-bms", "VERY_LONG_SCREEN_ID", "3270");
    }
}
