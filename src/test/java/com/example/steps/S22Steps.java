package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.uinavigation.model.*;
import com.example.domain.uinavigation.repository.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Suite
@SelectClasspathResource("features")
public class S22Steps {

    private final InMemoryScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate screenMap;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        // ID defaults to "DEMO_SCREEN" used in test context
        screenMap = repository.findById("DEMO_SCREEN");
        assertNotNull(screenMap, "ScreenMap aggregate should be initialized by repo");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Implicitly handled by the ScreenMap aggregate setup
        assertNotNull(screenMap.id());
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Implicitly handled by the specific command construction in the When step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // Valid input matching the DEMO_SCREEN map
            Map<String, String> validInputs = Map.of(
                "NAME", "John Doe",
                "PHONE", "5551234"
            );
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), validInputs);
            screenMap.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        assertFalse(screenMap.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        assertEquals("input.validated", screenMap.uncommittedEvents().get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        screenMap = repository.findById("DEMO_SCREEN");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_command_is_executed_missing_mandatory() {
        try {
            // Missing mandatory field 'NAME'
            Map<String, String> invalidInputs = Map.of(
                "PHONE", "5551234"
            );
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), invalidInputs);
            screenMap.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(capturedException.getMessage().contains("mandatory"), "Error message should mention 'mandatory'");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_field_lengths() {
        screenMap = repository.findById("DEMO_SCREEN");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_command_is_executed_invalid_length() {
        try {
            // 'NAME' exceeds BMS length of 10
            Map<String, String> invalidInputs = Map.of(
                "NAME", "This name is way too long",
                "PHONE", "5551234"
            );
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), invalidInputs);
            screenMap.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
