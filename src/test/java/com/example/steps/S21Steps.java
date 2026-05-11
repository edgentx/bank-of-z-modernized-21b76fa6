package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SM-001");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Command is constructed in the When step, but we ensure validity here for the happy path
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Command is constructed in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Defaulting to valid values if called from happy path
            String screenId = "LOGIN"; // Valid BMS length
            String deviceType = "3270";   // Valid BMS length
            command = new RenderScreenCmd("SM-001", screenId, deviceType);
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN", event.screenId());
        assertEquals("3270", event.deviceType());
    }

    // Negative Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_fields() {
        aggregate = new ScreenMapAggregate("SM-002");
    }

    @When("the RenderScreenCmd command is executed with blank screenId")
    public void execute_command_blank_screen_id() {
        try {
            command = new RenderScreenCmd("SM-002", "", "3270");
            resultingEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_long_fields() {
        aggregate = new ScreenMapAggregate("SM-003");
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void execute_command_long_screen_id() {
        try {
            // BMS Limit is 10 chars
            command = new RenderScreenCmd("SM-003", "VERY_LONG_SCREEN_ID", "3270");
            resultingEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertNull(resultingEvents);
    }
}