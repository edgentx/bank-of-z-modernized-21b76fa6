package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        String screenMapId = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(screenMapId);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context setup usually handled in the 'When' block via Command construction,
        // but we ensure the aggregate instance exists.
        if (aggregate == null) {
            a_valid_screen_map_aggregate();
        }
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        if (aggregate == null) {
            a_valid_screen_map_aggregate();
        }
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_mandatory_fields() {
        // State is captured in the Command, not the aggregate, for this validation.
        // We just need a valid aggregate instance to accept the command.
        a_valid_screen_map_aggregate();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_field_lengths() {
        a_valid_screen_map_aggregate();
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Scenario 1: Success
        if (!Thread.currentThread().getStackTrace()[2].getMethodName().contains("missing_mandatory") &&
            !Thread.currentThread().getStackTrace()[2].getMethodName().contains("invalid_field_lengths")) {
            
            RenderScreenCmd cmd = new RenderScreenCmd("SCRN01", "DESKTOP");
            executeCommand(cmd);
        }
        
        // Scenario 2: Missing Mandatory Fields (e.g., null screenId)
        if (Thread.currentThread().getStackTrace()[2].getMethodName().contains("missing_mandatory")) {
            RenderScreenCmd cmd = new RenderScreenCmd(null, "DESKTOP");
            executeCommand(cmd);
        }

        // Scenario 3: Field Lengths (e.g., deviceType too long)
        if (Thread.currentThread().getStackTrace()[2].getMethodName().contains("invalid_field_lengths")) {
            RenderScreenCmd cmd = new RenderScreenCmd("SCRN01", "THIS_DEVICE_TYPE_IS_FAR_TOO_LONG_FOR_LEGACY_BMS_CONSTRAINTS");
            executeCommand(cmd);
        }
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
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

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Exception should be a domain error (IllegalArgumentException)");
    }

    // Test Suite Hook
    @org.junit.platform.suite.api.Suite
    @org.junit.platform.suite.api.IncludeEngines("cucumber")
    @org.junit.platform.suite.api.SelectClasspathResource("features/S-21.feature")
    public static class S21TestSuite {}
}
