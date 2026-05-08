package com.example.steps;

import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the 'when' step via command construction, or state setup if needed.
        // For this scenario, the Command carries the screenId.
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the 'when' step via command construction.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command for the happy path
        RenderScreenCmd cmd = new RenderScreenCmd("SCREEN-001", "DESKTOP");
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCREEN-ERR-001");
    }

    @When("the RenderScreenCmd command is executed with invalid input")
    public void the_render_screen_cmd_command_is_executed_with_invalid_input() {
        // Simulating violation: screenId is blank
        RenderScreenCmd cmd = new RenderScreenCmd("", "DESKTOP");
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("SCREEN-ERR-002");
    }

    @When("the RenderScreenCmd command is executed with invalid BMS lengths")
    public void the_render_screen_cmd_command_is_executed_with_invalid_bms_lengths() {
        // Simulating violation: deviceType exceeds BMS max length (e.g., 8 chars)
        String longDeviceType = "EXTRALONGDEVICE";
        RenderScreenCmd cmd = new RenderScreenCmd("SCREEN-002", longDeviceType);
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
