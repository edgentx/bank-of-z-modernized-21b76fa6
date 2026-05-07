package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private Aggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup, command constructed in When step or specific context methods
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command construction for the happy path
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "3270");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        // Simulate missing field (e.g. null screenId)
        this.cmd = new RenderScreenCmd("screen-map-1", null, "3270");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
        // Simulate BMS constraint violation (e.g., screenId > 8 chars)
        this.cmd = new RenderScreenCmd("screen-map-1", "THIS_SCREEN_ID_IS_TOO_LONG", "3270");
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Checking for IllegalArgumentException which is the standard Java domain error for invalid input
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    @When("the RenderScreenCmd command is executed")
    public void executeRenderScreenCmd() {
        the_render_screen_cmd_command_is_executed();
    }
}
