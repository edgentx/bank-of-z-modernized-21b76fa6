package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
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
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // No-op, handled in When
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // No-op, handled in When
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-invalid-fields");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("map-invalid-bms");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // We determine the command payload based on the context (Givens).
        // Since Cucumber contexts are simple here, we infer intent.
        // In a real app, we might use a Scenario Context.
        
        if (aggregate.id().equals("map-invalid-fields")) {
            // Missing screenId
            command = new RenderScreenCmd(aggregate.id(), null, "3270", 40);
        } else if (aggregate.id().equals("map-invalid-bms")) {
            // Exceeds 80 char limit
            command = new RenderScreenCmd(aggregate.id(), "SCRN01", "3270", 81);
        } else {
            // Valid Command
            command = new RenderScreenCmd(aggregate.id(), "SCRN01", "Browser", 40);
        }

        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("SCRN01", event.screenId());
        assertEquals("Browser", event.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
