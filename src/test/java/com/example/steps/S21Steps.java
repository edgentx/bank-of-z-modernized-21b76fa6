package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("test-screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Cmd construction is completed in the 'When' step to allow variations
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Cmd construction is completed in the 'When' step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        cmd = new RenderScreenCmd("LOGIN01", "3270");
        executeCommand();
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + (capturedException != null ? capturedException.getMessage() : ""));
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("test-screen-map-1", event.aggregateId());
        assertEquals("LOGIN01", event.screenId());
        assertEquals("3270", event.deviceType());
        assertNotNull(event.layout());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("test-screen-map-err");
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_render_screen_cmd_command_is_executed_with_invalid_data() {
        // screenId is null/blank
        cmd = new RenderScreenCmd("", "3270");
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("required"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        aggregate = new ScreenMapAggregate("test-screen-map-len");
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void the_render_screen_cmd_command_is_executed_with_invalid_length() {
        // screenId length > 8
        cmd = new RenderScreenCmd("LONGSCREENNAME", "3270");
        executeCommand();
    }

    private void executeCommand() {
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
