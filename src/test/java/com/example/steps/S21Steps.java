package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("map-123");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Default valid setup, will be overridden in negative scenarios
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("map-123", "LOGIN01", "3270");
        } else {
            // Preserve the existing command instance if created in a violation step, but fix ID
             this.cmd = new RenderScreenCmd("map-123", this.cmd.screenId(), this.cmd.deviceType());
        }
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("map-123", "LOGIN01", "3270");
        } else {
            this.cmd = new RenderScreenCmd("map-123", this.cmd.screenId(), this.cmd.deviceType());
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("map-123");
        this.cmd = new RenderScreenCmd("map-123", null, "3270"); // screenId is null
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("map-123");
        // Assume legacy BMS max field length is 8. "MAINMENU_LOGIN" is > 8.
        this.cmd = new RenderScreenCmd("map-123", "MAINMENU_LOGIN", "3270");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
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

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
