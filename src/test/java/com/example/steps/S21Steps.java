package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Constants based on BMS constraints from the aggregate
    private static final int MAX_LENGTH = 30;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("sm-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // State setup, used in conjunction with other steps
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // State setup
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command execution
        executeCommand("LOGIN_SCREEN", "3270_TERMINAL");
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    // --- Failure Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_input_fields() {
        aggregate = new ScreenMapAggregate("sm-invalid-input");
    }

    @When("the RenderScreenCmd command is executed with invalid inputs")
    public void the_render_screen_cmd_command_is_executed_with_invalid_inputs() {
        // Violation: Null/Blank inputs
        executeCommand("", ""); // triggers blank validation
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("sm-invalid-length");
    }

    @When("the RenderScreenCmd command is executed with excessive lengths")
    public void the_render_screen_cmd_command_is_executed_with_excessive_lengths() {
        // Violation: Exceeds 30 chars
        String longId = "TOO_LONG_SCREEN_ID_THAT_EXCEEDS_BMS";
        executeCommand(longId, "TERMINAL");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain error, but command succeeded");
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Helper
    private void executeCommand(String screenId, String deviceType) {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
