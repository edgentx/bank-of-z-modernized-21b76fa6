package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the 'When' step construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the 'When' step construction
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Create a valid command instance
        Command cmd = new RenderScreenCmd("screen-01", "3270", 80, 24);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext(), "Expected at least one event");
        assertTrue(resultEvents.iterator().next() instanceof ScreenRenderedEvent, "Expected ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_command_is_executed_with_invalid_data() {
        // Command with null screenId to trigger validation error
        Command cmd = new RenderScreenCmd(null, "3270", 80, 24);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-bms-fail");
    }

    @When("the RenderScreenCmd command is executed with BMS-violating data")
    public void the_command_is_executed_with_bms_violating_data() {
        // BMS constraint: width <= 80. Testing 81.
        Command cmd = new RenderScreenCmd("screen-bms-fail", "3270", 81, 24);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception but none was thrown");
        // Verifying it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
