package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.RenderScreenCmd;
import com.example.domain.uimodel.ScreenMapAggregate;
import com.example.domain.uimodel.ScreenRenderedEvent;
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
        // Command construction logic handled in When steps or context storage
        // This step validates pre-condition existence.
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Command construction logic handled in When steps
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Defaults for successful path if not overridden by previous 'invalid' steps
        if (command == null) {
            command = new RenderScreenCmd("LOGIN001", "3270");
        }
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
        assertEquals(ScreenRenderedEvent.class, resultEvents.get(0).getClass());
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_all_mandatory_input_fields() {
        aggregate = new ScreenMapAggregate("map-invalid");
        command = new RenderScreenCmd(null, "3270"); // Violates mandatory screenId
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("map-invalid-len");
        command = new RenderScreenCmd("VERY_LONG_SCREEN_ID_123", "3270"); // Exceeds length of 10
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}