package com.example.steps;

import com.example.domain.routing.model.ScreenMapAggregate;
import com.example.domain.routing.model.ValidateScreenInputCmd;
import com.example.domain.routing.model.ScreenInputValidatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        // Using a dummy ID for the aggregate root
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        
        // Configure mock fields: USER (mandatory, len 10), PASS (mandatory, len 20)
        aggregate.configureField("USER", true, 10);
        aggregate.configureField("PASS", true, 20);
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Screen ID is implicitly handled by the aggregate instantiation in the previous step
        // or can be explicitly passed in the command context later.
        // For this scenario, we assume the command targets the aggregate's ID.
    }

    @And("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Input fields are constructed in the 'When' step to ensure freshness
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> validInputs = new HashMap<>();
        validInputs.put("USER", "admin");
        validInputs.put("PASS", "secret");
        
        var cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", validInputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent, "Expected ScreenInputValidatedEvent");
        assertEquals("input.validated", event.type());
    }

    // --- Scenario 2: Mandatory Fields ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("TRANSFER_SCREEN");
        aggregate.configureField("AMOUNT", true, 10); // Mandatory
        aggregate.configureField("TO_ACCOUNT", true, 12); // Mandatory
        aggregate.configureField("REFERENCE", false, 20); // Optional
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_command_is_executed_with_missing_mandatory() {
        // Missing 'TO_ACCOUNT'
        Map<String, String> incompleteInputs = new HashMap<>();
        incompleteInputs.put("AMOUNT", "100");
        
        var cmd = new ValidateScreenInputCmd("TRANSFER_SCREEN", incompleteInputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown for missing mandatory fields");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(thrownException.getMessage().contains("missing"), "Error message should mention missing field");
    }

    // --- Scenario 3: Length Constraints ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_length_constraints() {
        aggregate = new ScreenMapAggregate("PROFILE_SCREEN");
        aggregate.configureField("NAME", true, 5); // Legacy constraint is very short
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_command_is_executed_with_invalid_length() {
        Map<String, String> longInputs = new HashMap<>();
        longInputs.put("NAME", "Alexander"); // Length 9 > 5
        
        var cmd = new ValidateScreenInputCmd("PROFILE_SCREEN", longInputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_for_length() {
        assertNotNull(thrownException, "Expected an exception to be thrown for length violation");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(thrownException.getMessage().contains("exceeds maximum"), "Error message should mention length constraint");
    }
}
