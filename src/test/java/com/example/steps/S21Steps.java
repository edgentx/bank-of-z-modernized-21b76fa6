package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Setup handled in command creation within 'When'
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Setup handled in command creation within 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            Command cmd = new RenderScreenCmd("screen-123", "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @When("the command is executed with missing mandatory fields")
    public void the_command_is_executed_with_missing_mandatory_fields() {
        try {
            // Screen ID is null
            Command cmd = new RenderScreenCmd(null, "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMapAggregate("screen-bms-fail");
    }

    @When("the command is executed with invalid field lengths")
    public void the_command_is_executed_with_invalid_field_lengths() {
        try {
            // Device type exceeds legacy BMS constraints (assuming max 8 chars for BMS field)
            String invalidDeviceType = "MODERN_ANDROID_DEVICE_TYPE"; 
            Command cmd = new RenderScreenCmd("screen-bms-fail", invalidDeviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
        Assertions.assertNull(resultEvents || resultEvents.isEmpty(), "No events should be emitted on failure");
    }

    // Helper to verify empty or null list in Java
    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    @Then("the command is rejected with a domain error")
    public void validate_rejection() {
        the_command_is_rejected_with_a_domain_error();
    }
}
