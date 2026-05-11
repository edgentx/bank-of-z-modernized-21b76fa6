package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("map-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Command construction is deferred to the 'When' step to allow parameter variation
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Command construction is deferred to the 'When' step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid values if not overridden in specific 'Given' violation scenarios
        if (command == null) {
            command = new RenderScreenCmd("LOGIN01", "3270");
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event type should be ScreenRenderedEvent");
        assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-01");
        // Setup command with null/blank screenId to violate validation
        command = new RenderScreenCmd(null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("map-01");
        // Setup command with screenId > 8 chars to violate BMS legacy constraint
        command = new RenderScreenCmd("MAIN_MENU_SCR", "3270");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be an IllegalArgumentException (Domain Error)");
        assertNull(resultEvents, "No events should be emitted on error");
    }

}
