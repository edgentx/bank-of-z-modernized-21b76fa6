package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate screenMap;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        screenMap = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context managed in the When step via command object
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context managed in the When step via command object
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command for the success scenario
        RenderScreenCmd cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCR", "3270", List.of());
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        screenMap = new ScreenMapAggregate("screen-map-1");
    }

    @When("the RenderScreenCmd command is executed with empty screenId")
    public void the_render_screen_cmd_is_executed_with_empty_screenId() {
        RenderScreenCmd cmd = new RenderScreenCmd("screen-map-1", "", "3270", List.of());
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        screenMap = new ScreenMapAggregate("screen-map-1");
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void the_render_screen_cmd_is_executed_with_invalid_field_lengths() {
        // BMS constraint: Field name usually max 12 chars, screen definition often 24x80 or similar constraints.
        // We simulate a field name exceeding the legacy buffer limit.
        RenderScreenCmd cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCR", "3270", List.of(new FieldDefinition("VERY_LONG_FIELD_NAME_EXCEEDS_BMS_LIMIT", 1, 1, 10)));
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultingEvents.size(), "One event should be emitted");
        Assertions.assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        Assertions.assertEquals("screen.rendered", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "An exception should have been thrown");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException (Domain Error)");
    }

    private void executeCommand(Command cmd) {
        try {
            resultingEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
