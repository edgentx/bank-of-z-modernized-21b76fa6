package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // State is managed within the aggregate or command construction in 'When'
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // State is managed within the aggregate or command construction in 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("screen-123", "desktop");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the RenderScreenCmd command is executed with invalid fields")
    public void the_render_screen_cmd_command_is_executed_with_invalid_fields() {
        try {
            // screenId is blank
            RenderScreenCmd cmd = new RenderScreenCmd("", "desktop");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-bms");
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void the_render_screen_cmd_command_is_executed_with_invalid_length() {
        try {
            // deviceType exceeds 10 chars
            RenderScreenCmd cmd = new RenderScreenCmd("screen-bms", "very-long-device-type");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // Reusable Then step for domain error scenarios
    // Note: In a real setup, we might map specific error text, but type checking suffices for now.
}
