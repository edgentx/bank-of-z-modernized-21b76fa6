package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
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
        // Initialize with a valid ID for the happy path
        this.aggregate = new ScreenMapAggregate("screen-01");
        // Pre-load valid state for the happy path
        this.aggregate.hydrate("Valid Screen", 1920, 1080);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled within aggregate state initialization
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled via command construction in 'When'
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_invalid_fields() {
        // Create an aggregate with empty/null title to trigger validation error
        this.aggregate = new ScreenMapAggregate("screen-invalid-01");
        this.aggregate.hydrate(null, 100, 100); // Title is mandatory
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_exceeding_bms_constraints() {
        // Title length > 50 (BMS limit)
        String longTitle = "This screen title is intentionally way too long for BMS constraints";
        this.aggregate = new ScreenMapAggregate("screen-bms-fail");
        this.aggregate.hydrate(longTitle, 100, 100);
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Using standard desktop dimensions for validation
        RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), "DESKTOP", 1920, 1080);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        } catch (UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertNull(capturedException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Exception should be a domain logic error (IllegalArgumentException or IllegalStateException)");
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank(),
            "Exception should have a descriptive message");
    }
}
