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
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        this.capturedException = null;
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Set up default valid command parts
        // We assume a valid ID fits BMS constraints (<= 8 chars)
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("screen-map-1", "LOGIN001", ScreenMapAggregate.DeviceType.WEB_DESKTOP);
        } else {
            // If command was partially created in a violation step, fix it here for the 'And' clause logic
            // (In this specific scenario structure, we build fresh valid data)
            this.cmd = new RenderScreenCmd("screen-map-1", "LOGIN001", ScreenMapAggregate.DeviceType.WEB_DESKTOP);
        }
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        if (this.cmd != null && this.cmd.screenId().equals("LOGIN001")) {
            // Already set in previous step
        } else {
            this.cmd = new RenderScreenCmd("screen-map-1", "LOGIN001", ScreenMapAggregate.DeviceType.WEB_DESKTOP);
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("screen-map-bad");
        // Violation: Missing screenId (blank)
        this.cmd = new RenderScreenCmd("screen-map-bad", "", ScreenMapAggregate.DeviceType.WEB_DESKTOP);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("screen-map-len");
        // Violation: Screen ID > 8 chars (BMS Mapset name limit)
        this.cmd = new RenderScreenCmd("screen-map-len", "TOO_LONG_SCREEN_ID", ScreenMapAggregate.DeviceType.TERMINAL_3270);
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            this.resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertEquals("screen.rendered", resultingEvents.get(0).type());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertNotNull(event.layout());
        assertTrue(event.layout().contains("LOGIN001"));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}