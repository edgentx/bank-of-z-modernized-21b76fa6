package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup handled in When step
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context setup handled in When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd(
            "screen-map-1",
            "LOGIN_SCREEN",
            "DESKTOP",
            null,
            Map.of("username", "testUser", "password", "pwd123")
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCREEN", event.screenId());
        assertEquals("DESKTOP", event.deviceType());
    }

    // Rejection Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid-input");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-map-invalid-length");
    }

    // Using specific When methods to differentiate the command creation for rejection scenarios
    @When("the RenderScreenCmd command is executed with invalid input")
    public void the_render_screen_cmd_command_is_executed_with_invalid_input() {
        // Violation: null inputData
        RenderScreenCmd cmd = new RenderScreenCmd(
            "screen-map-invalid-input",
            "HOME_SCREEN",
            "MOBILE",
            null,
            null // Violation
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void the_render_screen_cmd_command_is_executed_with_invalid_length() {
        // Violation: screenId > 32 chars
        String longScreenId = "VERY_LONG_SCREEN_NAME_THAT_EXCEEDS_LIMIT";
        RenderScreenCmd cmd = new RenderScreenCmd(
            "screen-map-invalid-length",
            longScreenId,
            "TABLET",
            null,
            Map.of("key", "value")
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
