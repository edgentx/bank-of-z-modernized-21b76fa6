package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        String screenMapId = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(screenMapId);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // State managed in the aggregate initialization or specific command construction
        // Intentionally empty: Specifics handled in the 'When' step construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Intentionally empty
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        executeCommand(new RenderScreenCmd(aggregate.id(), "SCRN01", DeviceType.DESKTOP_WEB));
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no error, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        String screenMapId = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(screenMapId);
    }

    @When("the RenderScreenCmd command is executed with missing screenId")
    public void the_RenderScreenCmd_command_is_executed_with_missing_screenId() {
        // screenId is null
        executeCommand(new RenderScreenCmd(aggregate.id(), null, DeviceType.DESKTOP_WEB));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception but command succeeded");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        String screenMapId = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(screenMapId);
    }

    @When("the RenderScreenCmd command is executed with invalid field length")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_field_length() {
        // screenId exceeds max length
        String longScreenId = "SCREEN-ID-IS-TOO-LONG-FOR-LEGACY-BMS";
        executeCommand(new RenderScreenCmd(aggregate.id(), longScreenId, DeviceType.DESKTOP_WEB));
    }

    // Helper method to execute command and capture exceptions
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
