package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Command construction happens in the When step for flexibility, or we store state here.
        // For simplicity in this pattern, we'll build the command in the When step.
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Command construction happens in the When step.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command construction if not modified by 'Given violations'
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCREEN", "3270", Map.of("user", "field"));
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
        // Create a command with blank screenId to violate the rule
        cmd = new RenderScreenCmd("screen-map-invalid", "", "3270", Map.of());
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-map-bms-violation");
        // Create a context key longer than 30 chars (BMS limit)
        String longKey = "very_long_field_name_exceeding_bms_limit";
        cmd = new RenderScreenCmd("screen-map-bms-violation", "DEPOSIT", "3270", Map.of(longKey, "value"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // In Java DDD, domain errors are often IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(IllegalArgumentException.class.isAssignableFrom(capturedException.getClass()) ||
                        IllegalStateException.class.isAssignableFrom(capturedException.getClass()),
                "Exception should be a domain error (IAE or ISE)");
    }
}
