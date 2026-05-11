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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("map-123");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // We store this in a way that the 'When' step can construct the command
        // For simplicity in this step, we assume the command construction happens in the When step
        // or we store attributes. Let's store attributes in a more complex test, but here
        // we'll construct the valid command in the 'When' step.
        // Alternatively, we can define the command here if the scenario implies it.
        // However, given the structure, I'll initialize the command variables or construct in When.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Same as above.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Scenario 1: Happy path - defaults to valid data if not set by previous negative Given
        if (cmd == null) {
            cmd = new RenderScreenCmd("map-123", "ACCTSUM", "3270");
        }
        executeCommand();
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("ACCTSUM", event.screenId());
        assertEquals("3270", event.deviceType());
        assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-123");
        // Create command with null/blank screenId
        cmd = new RenderScreenCmd("map-123", null, "3270");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("map-123");
        // Create command with screenId > 8 chars
        cmd = new RenderScreenCmd("map-123", "LONGSCREENID123", "3270");
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}