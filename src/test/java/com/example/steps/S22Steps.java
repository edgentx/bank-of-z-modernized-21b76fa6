package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.ScreenInputValidatedEvent;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ValidateScreenInputCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class S22Steps {

    // Test State
    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd currentCommand;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Constants for Scenarios
    private static final String SCREEN_ID = "LOGIN_SCREEN";
    private static final String FIELD_USER = "USERNAME";
    private static final String FIELD_PASS = "PASSWORD";

    // Setup Data
    private Set<String> mandatoryFields = new HashSet<>();
    private Map<String, Integer> lengthConstraints = new HashMap<>();
    private Map<String, String> inputFields = new HashMap<>();

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("SCREEN_MAP_001");
        thrownException = null;
        resultEvents = null;
        
        // Common legacy constraints
        lengthConstraints.put(FIELD_USER, 8);  // BMS constraint
        lengthConstraints.put(FIELD_PASS, 24); // BMS constraint
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Handled by command construction in @When
    }

    @And("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        inputFields.put(FIELD_USER, "ALICE"); // Valid length
        inputFields.put(FIELD_PASS, "password123"); // Valid length
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        try {
            // Scenario 1 uses valid data setup in @Given
            if (inputFields.isEmpty() && thrownException == null) {
                // Default valid data if not overridden by violation steps
                inputFields.put(FIELD_USER, "ALICE");
                inputFields.put(FIELD_PASS, "PASS");
            }
            
            currentCommand = new ValidateScreenInputCmd(
                SCREEN_ID,
                inputFields,
                mandatoryFields,
                lengthConstraints
            );
            
            resultEvents = aggregate.execute(currentCommand);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception: " + thrownException);
        Assertions.assertNotNull(resultEvents, "Result events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException domain error");
    }

    // Scenario 2: Violation Setup
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        // Given a valid aggregate base
        a_valid_screen_map_aggregate();
        
        // And add constraints
        mandatoryFields.add(FIELD_USER);
        mandatoryFields.add(FIELD_PASS);
        
        // Setup input to violate constraint: PASSWORD is missing
        inputFields.put(FIELD_USER, "BOB");
        // PASSWORD is intentionally omitted from inputFields
    }

    // Scenario 3: Violation Setup
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_length() {
        // Given a valid aggregate base
        a_valid_screen_map_aggregate();
        
        // Setup input to violate constraint: USERNAME exceeds length 8
        inputFields.put(FIELD_USER, "ALEXANDER_THE_GREAT"); // Length > 8
        inputFields.put(FIELD_PASS, "SECRET");
    }

}
