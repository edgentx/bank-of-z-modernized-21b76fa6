package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("SCREEN-001");
        // Pre-hydrate state to simulate loaded aggregate
        this.aggregate.hydrate("CustomerMaintenance", "BMS-Mainframe-Def", 1);
        this.capturedException = null;
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled implicitly by the aggregate setup or command construction in 'When'
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled implicitly by the aggregate setup or command construction in 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Assuming the aggregate is already in a valid state to render
        RenderScreenCmd cmd = new RenderScreenCmd("SCREEN-001", "3270-Terminal");
        try {
            resultEvents = aggregate.execute(cmd);
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
        assertNotNull(event.layout());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("SCREEN-002");
        // Simulate a state where mandatory fields (e.g. source definition) are missing
        // The aggregate logic will catch the missing source in the execute method if passed a command that requires context
        // or the command itself is invalid. Here we set the aggregate state to be 'invalid' for rendering.
        this.aggregate.hydrate(null, "Legacy-BMS", 0); // Missing screen name/source
    }

    @When("the RenderScreenCmd command is executed with invalid context")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidContext() {
        // We pass a valid command, but the aggregate state is invalid
        RenderScreenCmd cmd = new RenderScreenCmd("SCREEN-002", "Web-Mobile");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        this.aggregate = new ScreenMapAggregate("SCREEN-003");
        // Setup aggregate with a screen ID that implies BMS constraints
        // The violation will occur if we attempt to render a layout incompatible with BMS
        // However, the scenario says the command is rejected.
        // Let's assume the command requests a layout width exceeding BMS limits for this screen.
        this.aggregate.hydrate("BMS-Critical-Screen", "Strict-BMS", 1);
    }

    @When("the RenderScreenCmd command is executed exceeding constraints")
    public void theRenderScreenCmdCommandIsExecutedExceedingConstraints() {
        // We pass a device type that implies a layout width violating BMS constraints (e.g. 1920px)
        // The command or aggregate should validate this.
        RenderScreenCmd cmd = new RenderScreenCmd("SCREEN-003", "Ultra-Wide-Monitor-1920");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        // Check error message content for specificity (optional but good)
    }
}
