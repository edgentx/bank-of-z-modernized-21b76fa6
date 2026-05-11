package com.example.steps;

import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate screenMap;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        screenMap = new ScreenMapAggregate("screen-001");
        // Define fields for testing
        screenMap.defineField("ACCOUNT_NUM", 10, true);  // Mandatory, max 10 chars
        screenMap.defineField("TRANS_CODE", 3, false);   // Optional, max 3 chars
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Parameterized in the 'When' step, handled implicitly by command construction
    }

    @And("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided() {
        // Parameterized in the 'When' step
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        screenMap = new ScreenMapAggregate("screen-001");
        screenMap.defineField("ACCOUNT_NUM", 10, true); // Mandatory
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        screenMap = new ScreenMapAggregate("screen-001");
        screenMap.defineField("ACCOUNT_NUM", 10, true);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void the_ValidateScreenInputCmd_command_is_executed() {
        try {
            // We construct the command based on the scenario context implicitly
            // Scenario 1: Success
            if (caughtException == null) {
                // This logic is simplified for the BDD runner. In a real runner, we might use scenario context.
                // Assuming success case default for the 'valid' steps
                ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("screen-001", Map.of("ACCOUNT_NUM", "123456", "TRANS_CODE", "01"));
                // If this is the violation test for length (deterministic check via name logic or explicit scenario)
                // Cucumber runs scenarios in isolation, so we can't easily switch logic here without context.
                // Instead, we rely on specific Given/When flows or catch the exception.
                // However, the standard pattern is to execute and capture result.
                // To support multiple scenarios, we usually need a context object or
                // specific step definitions for specific cases.
                // For simplicity in this file, I will assume the 'valid' case runs here,
                // and the exception cases are triggered by setup.
                
                // NOTE: A robust Cucumber implementation often uses a specific DataTable or Examples clause.
                // Given the constraints, I'll implement a dispatcher check based on the aggregate state if possible,
                // or rely on the test runner calling specific steps.
                // Here, I'll implement a generic executor that handles the command passed from the context.
                
                // Actually, let's look at the 'violation' givens. They set up the aggregate.
                // The When step is shared. We need to know *which* data to send.
                // I will assume the 'valid' case sends valid data, and the violation cases send invalid data.
                // This is a slight simplification of the Gherkin flow.
            }
            
            // Since the 'When' is shared, we need to determine the intent. 
            // Ideally, the 'And' steps set the input data in a context.
            // Let's introduce a simple context holder for the input fields to be used by the When step.
        } catch (Exception e) {
            // This block might catch validation errors if we weren't delegating properly
        }
    }

    // Refined approach for the shared 'When' step:
    // We will store the command data in the test instance fields during the 'And' steps.
    private Map<String, String> currentInputFields;
    private String currentScreenId = "screen-001";

    @And("a valid inputFields is provided")
    public void a_valid_inputFields_is_provided_setter() {
        this.currentInputFields = Map.of("ACCOUNT_NUM", "123456", "TRANS_CODE", "01");
    }

    // Helper to trigger the 'mandatory' violation scenario (missing field)
    @When("the ValidateScreenInputCmd command is executed with missing mandatory field")
    public void the_ValidateScreenInputCmd_command_is_executed_missing() {
        this.currentInputFields = Map.of("TRANS_CODE", "01"); // Missing ACCOUNT_NUM
        executeCommand();
    }

    // Helper to trigger the 'length' violation scenario
    @When("the ValidateScreenInputCmd command is executed with long field")
    public void the_ValidateScreenInputCmd_command_is_executed_long() {
        this.currentInputFields = Map.of("ACCOUNT_NUM", "12345678901"); // Length 11 > 10
        executeCommand();
    }

    // The generic executor
    private void executeCommand() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(currentScreenId, currentInputFields);
            resultEvents = screenMap.execute(cmd);
            caughtException = null;
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    // Standard When for the success path
    @When("the ValidateScreenInputCmd command is executed")
    public void the_command_is_executed_generic() {
        // Only execute if we have valid inputs set, otherwise do nothing or wait for specific hook.
        // For the success scenario, 'a_valid_inputFields_is_provided' sets currentInputFields.
        if (currentInputFields != null) {
             executeCommand();
        }
    }

    @Then("a input.validated event is emitted")
    public void a_input_validated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null (exception occurred: " + caughtException + ")");
        assertEquals(1, resultEvents.size());
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error (Exception), but command succeeded.");
        // We verify it's an IllegalStateException or similar as per our aggregate logic
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
