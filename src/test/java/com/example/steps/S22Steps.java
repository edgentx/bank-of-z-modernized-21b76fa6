package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private Aggregate screenMap;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Given: a valid ScreenMap aggregate
    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        // Initialize a new aggregate. In a real scenario, we might load a specific map definition.
        // For S-22, we assume 'TEST_SCRN_01' exists.
        screenMap = new ScreenMap("TEST_SCRN_01");
        // Configure some fields to simulate a loaded BMS map
        ((ScreenMap) screenMap).configureField("ACCOUNT_NUM", true, 10);
        ((ScreenMap) screenMap).configureField("TX_AMOUNT", false, 12);
    }

    // Given: a ScreenMap aggregate that violates mandatory fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        a_valid_ScreenMap_aggregate(); // Set up standard map
    }

    // Given: a ScreenMap aggregate that violates length constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_length_constraints() {
        a_valid_ScreenMap_aggregate(); // Set up standard map
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Nothing specific to do here, will be set in command construction
    }

    @And("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Nothing specific to do here, will be set in command construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        try {
            // Build a default command for the successful path or based on context
            // For negative scenarios, we manipulate the map or input directly in specific step variations if needed,
            // but here we assume the steps cover the positive case and we use specific input for negative ones below.
            if (cmd == null) {
                 Map<String, String> inputs = new HashMap<>();
                 inputs.put("ACCOUNT_NUM", "12345");
                 inputs.put("TX_AMOUNT", "100.00");
                 cmd = new ValidateScreenInputCmd("TEST_SCRN_01", inputs);
            }
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Specific When for mandatory failure
    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void the_ValidateScreenInputCmd_command_is_executed_with_missing_mandatory_fields() {
        Map<String, String> inputs = new HashMap<>();
        // Intentionally missing 'ACCOUNT_NUM'
        inputs.put("TX_AMOUNT", "100.00");
        cmd = new ValidateScreenInputCmd("TEST_SCRN_01", inputs);
        
        try {
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Specific When for length failure
    @When("the ValidateScreenInputCmd command is executed with invalid length fields")
    public void the_ValidateScreenInputCmd_command_is_executed_with_invalid_length_fields() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "12345");
        // Exceeds length of 12 defined in the map setup
        inputs.put("TX_AMOUNT", "1234567890123"); 
        cmd = new ValidateScreenInputCmd("TEST_SCRN_01", inputs);

        try {
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals("TEST_SCRN_01", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }
}