package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // State stored implicitly for the When step
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // State stored implicitly
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_violating_mandatory_fields() {
        aggregate = new ScreenMapAggregate("map-2");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_violating_bms_lengths() {
        aggregate = new ScreenMapAggregate("map-3");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Default valid data for success case
            String screenId = "LOGIN_SCREEN";
            String deviceType = "3270";

            // Check for violation data to trigger errors (simple heuristic based on description)
            if (aggregate.id().equals("map-2")) {
                screenId = null; // Violate mandatory
            } else if (aggregate.id().equals("map-3")) {
                screenId = "VERY_LONG_SCREEN_NAME_THAT_EXCEEDS_LEGACY_BMS_CONSTRAINTS_OF_80_CHARS_.............";
            }

            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        assertEquals("screen.rendered", resultEvents.get(0).type());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
