package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;

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
        // Screen ID is part of the aggregate context in this simple stateless model,
        // or handled implicitly by the command creation.
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Device type will be provided in the command execution step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("screen-123", "3270", "MOBILE");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull("Expected no exception", capturedException);
        assertNotNull("Expected events to be emitted", resultEvents);
        assertEquals("Expected one event", 1, resultEvents.size());
        assertTrue("Expected ScreenRenderedEvent", resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the RenderScreenCmd command is executed for missing fields")
    public void the_render_screen_cmd_command_is_executed_with_missing_fields() {
        try {
            // Passing null screenId to trigger validation error
            RenderScreenCmd cmd = new RenderScreenCmd(null, "Layout1", "DESKTOP");
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception", capturedException);
        assertTrue("Expected IllegalArgumentException", capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-bms");
    }

    @When("the RenderScreenCmd command is executed with invalid lengths")
    public void the_render_screen_cmd_command_is_executed_with_invalid_lengths() {
        try {
            // Create a layout name > 10 chars to violate BMS constraint
            String longLayoutName = "THIS_IS_A_VERY_LONG_LAYOUT_NAME";
            RenderScreenCmd cmd = new RenderScreenCmd("screen-bms", longLayoutName, "3270");
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
