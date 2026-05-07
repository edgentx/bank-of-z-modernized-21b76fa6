package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
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
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the 'When' step construction for simplicity in this context, 
        // or we can set a default state here. 
        // For this step definition, we assume the command construction in 'When' uses valid IDs.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the 'When' step construction.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Use valid defaults for the success case
            command = new RenderScreenCmd("SCRN01", "3270");
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should produce exactly one event");
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Command will be created with nulls in the When step
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_render_screen_cmd_command_is_executed_with_invalid_data() {
        try {
            // Trigger failure: null screenId
            command = new RenderScreenCmd(null, "3270");
            resultingEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("VERYLONGSCREENID");
    }

    @When("the RenderScreenCmd command is executed with invalid lengths")
    public void the_render_screen_cmd_command_is_executed_with_long_length() {
        try {
            // Trigger failure: screenId > 8 chars
            command = new RenderScreenCmd("VERYLONGSCREENID", "3270");
            resultingEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
