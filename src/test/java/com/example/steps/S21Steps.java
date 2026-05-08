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
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-01");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled in the When clause via constructor for simplicity in this context
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Handled in the When clause via constructor
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command data
        command = new RenderScreenCmd("screen-01", "3270", "MOBILE");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-bad");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("screen-length");
    }

    @When("the RenderScreenCmd command is executed with invalid fields")
    public void the_render_screen_cmd_command_is_executed_with_invalid_fields() {
        // Scenario 2: Missing fields
        command = new RenderScreenCmd("", "3270", "MOBILE"); // Empty screenId
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid lengths")
    public void the_render_screen_cmd_command_is_executed_with_invalid_lengths() {
        // Scenario 3: Invalid length
        command = new RenderScreenCmd("screen-01", "THIS_DEFINITION_IS_WAY_TOO_LONG_FOR_BMS", "MOBILE");
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNull(resultEvents);
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Mapper for scenario hooking (simplified)
    @When("the RenderScreenCmd command is executed")
    public void execute_check_context() {
        if (aggregate.id().equals("screen-bad")) {
            the_render_screen_cmd_command_is_executed_with_invalid_fields();
        } else if (aggregate.id().equals("screen-length")) {
            the_render_screen_cmd_command_is_executed_with_invalid_lengths();
        } else {
            the_render_screen_cmd_command_is_executed();
        }
    }
}