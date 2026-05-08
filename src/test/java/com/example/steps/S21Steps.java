package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup, handled in When block via command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup, handled in When block via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Defaults for happy path
        executeCommand("LOGIN_SCREEN", "3270", new HashMap<>());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-bad");
    }

    @When("the RenderScreenCmd command is executed with missing screenId")
    public void the_render_screen_cmd_command_is_executed_with_missing_screen_id() {
        executeCommand(null, "3270", new HashMap<>());
    }

    @When("the RenderScreenCmd command is executed with missing deviceType")
    public void the_render_screen_cmd_command_is_executed_with_missing_device_type() {
        executeCommand("LOGIN_SCREEN", null, new HashMap<>());
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-map-bms-violator");
    }

    @When("the RenderScreenCmd command is executed with excessive field length")
    public void the_render_screen_cmd_command_is_executed_with_excessive_field_length() {
        Map<String, String> values = new HashMap<>();
        values.put("INPUT_FIELD", "A".repeat(81)); // Exceeds 80
        executeCommand("LOGIN_SCREEN", "3270", values);
    }

    private void executeCommand(String screenId, String deviceType, Map<String, String> values) {
        try {
            command = new RenderScreenCmd(aggregate.id(), screenId, deviceType, values);
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("screen-map-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException);
        System.out.println("Caught expected domain error: " + caughtException.getMessage());
    }
}
