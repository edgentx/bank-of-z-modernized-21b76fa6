package com.example.steps;

import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMap aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Record for BMS field definitions
    record FieldDef(int length, boolean mandatory) {}

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        // Setup a standard screen map with valid fields
        Map<String, ScreenMap.FieldDefinition> fields = new HashMap<>();
        fields.put("ACCOUNT_NO", new ScreenMap.FieldDefinition(12, true));
        fields.put("AMOUNT", new ScreenMap.FieldDefinition(10, true));
        fields.put("REFERENCE", new ScreenMap.FieldDefinition(20, false));
        
        aggregate = new ScreenMap("SCREEN_01", fields);
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Screen ID is part of the command construction, logic handled in "When"
    }

    @Given("a valid inputFields is provided")
    public void a_valid_input_fields_is_provided() {
        // Input fields are part of the command construction, logic handled in "When"
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_validate_screen_input_cmd_command_is_executed() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "123456789"); // Valid
        inputs.put("AMOUNT", "100.00");        // Valid
        inputs.put("REFERENCE", "REF-001");    // Valid (Optional)

        cmd = new ValidateScreenInputCmd("SCREEN_01", "LOGIN_SCR", inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
        assertEquals("navigation.input.validated", event.type());
    }

    // Scenario 2: Mandatory fields missing
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_that_violates_mandatory_fields() {
        Map<String, ScreenMap.FieldDefinition> fields = new HashMap<>();
        fields.put("ACCOUNT_NO", new ScreenMap.FieldDefinition(12, true));
        fields.put("AMOUNT", new ScreenMap.FieldDefinition(10, true));
        aggregate = new ScreenMap("SCREEN_01", fields);
    }

    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void the_command_is_executed_with_missing_mandatory_fields() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "123456789"); // AMOUNT is missing

        cmd = new ValidateScreenInputCmd("SCREEN_01", "LOGIN_SCR", inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalArgumentException, "Should be IllegalArgumentException");
        assertTrue(thrownException.getMessage().contains("Mandatory field"));
    }

    // Scenario 3: BMS Length Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_that_violates_bms_constraints() {
        Map<String, ScreenMap.FieldDefinition> fields = new HashMap<>();
        fields.put("ACCOUNT_NO", new ScreenMap.FieldDefinition(12, true)); // Max 12 chars
        aggregate = new ScreenMap("SCREEN_01", fields);
    }

    @When("the ValidateScreenInputCmd command is executed with invalid length")
    public void the_command_is_executed_with_invalid_length() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "1234567890123"); // 13 chars > 12 limit

        cmd = new ValidateScreenInputCmd("SCREEN_01", "LOGIN_SCR", inputs);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
