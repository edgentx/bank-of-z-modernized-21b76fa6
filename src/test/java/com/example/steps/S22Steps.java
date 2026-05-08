package com.example.steps;

import com.example.domain.screenmap.model.*;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate screenMap;
    private Throwable actualError;
    private ScreenInputValidatedEvent lastEvent;

    // In-memory repo not strictly used by steps directly but good for context setup if needed
    private final InMemoryScreenMapRepository repo = new InMemoryScreenMapRepository();

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        screenMap = new ScreenMapAggregate("SCR-LOGIN-01");
        // In a real scenario, we might hydrate this from the repo
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Assuming the aggregate ID serves as the screenId for this context
        // or validation logic checks the aggregate context.
        // The aggregate in 'Given a valid ScreenMap' satisfies this.
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Placeholder: Valid fields will be passed in the When block
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        // This is for the success case
        Map<String, String> validFields = Map.of(
            "USER_ID", "ADMIN", 
            "PASSWORD", "*****"
        );
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), validFields);
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        screenMap = new ScreenMapAggregate("SCR-LOGIN-01");
        // The violation comes from the input provided in the subsequent step
    }

    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void the_command_is_executed_with_missing_fields() {
        // Missing USER_ID
        Map<String, String> incompleteFields = Map.of("PASSWORD", "*****");
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), incompleteFields);
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        screenMap = new ScreenMapAggregate("SCR-LOGIN-01");
    }

    @When("the ValidateScreenInputCmd command is executed with invalid length fields")
    public void the_command_is_executed_with_invalid_length() {
        // USER_ID too long for typical BMS (e.g. > 10)
        Map<String, String> longFields = Map.of("USER_ID", "TOO_LONG_USER_ID", "PASSWORD", "***");
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), longFields);
        executeCommand(cmd);
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNull(actualError, "Should not have thrown an error: " + (actualError != null ? actualError.getMessage() : ""));
        Assertions.assertNotNull(lastEvent, "Event should not be null");
        Assertions.assertEquals("input.validated", lastEvent.type());
        Assertions.assertEquals(screenMap.id(), lastEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(actualError, "Expected an exception but none was thrown");
        // In DDD, these are usually IllegalArgumentExceptions or IllegalStateExceptions
        Assertions.assertTrue(
            actualError instanceof IllegalArgumentException || actualError instanceof IllegalStateException,
            "Expected domain error exception"
        );
    }

    // Helper to dispatch command based on scenario state
    private void executeCommand(Command cmd) {
        actualError = null;
        lastEvent = null;
        try {
            var events = screenMap.execute(cmd);
            if (!events.isEmpty()) {
                Object e = events.get(0);
                if (e instanceof ScreenInputValidatedEvent) {
                    lastEvent = (ScreenInputValidatedEvent) e;
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            actualError = e;
        } catch (Exception e) {
            actualError = e;
        }
    }
}
