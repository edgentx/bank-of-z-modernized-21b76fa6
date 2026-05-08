package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Constants for valid data
    private static final String VALID_SCREEN_ID = "LOGIN_SCREEN";
    private static final Set<String> VALID_REQUIRED_FIELDS = Set.of("USER", "PASS");
    private static final Map<String, Integer> VALID_CONSTRAINTS = Map.of(
        "USER", 10,
        "PASS", 20
    );

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate(VALID_SCREEN_ID);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Command construction is finalized in the 'When' clause to allow variation
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Command construction is finalized in the 'When' clause
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        Map<String, String> inputs = Map.of("USER", "alice", "PASS", "secret123");
        cmd = new ValidateScreenInputCmd(VALID_SCREEN_ID, inputs, VALID_REQUIRED_FIELDS, VALID_CONSTRAINTS);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInput_validatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent, "Expected ScreenInputValidatedEvent");
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(VALID_SCREEN_ID, event.aggregateId());
    }

    // --- Scenarios for Rejection ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate(VALID_SCREEN_ID);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate(VALID_SCREEN_ID);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedWithInvalidData() {
        // Check context to determine which failure scenario we are in.
        // Since Cucumber scenarios are isolated, we can check state or just execute specific logic based on the Given.
        // However, to keep it clean, we will assume the specific 'Given' setup prepared the context.
        
        Map<String, String> inputs;
        
        // If we are testing mandatory fields
        // We'll inspect the aggregate or just simulate the call. 
        // Simplest way: The scenario runs sequentially. If the previous step was the 'violates mandatory' step,
        // we don't have a flag. Let's look at the aggregate ID or rely on specific methods.
        // Actually, the cleanest way in pure Java steps without shared state complexity is to 
        // check the scenario name, but that's brittle. 
        // Instead, we will define specific 'When' logic based on the 'Given' context?
        // No, Gherkin doesn't link them that way implicitly in code.
        // 
        // Workaround: We will look at the exception stack or set a flag in the Given steps.
        
        // Let's refine: The prompt implies specific scenarios. I will create separate helper methods or a flag.
        // But wait, the 'When' text is identical. 
        // I will check for a flag set by the Given methods.
    }
    
    // Overriding the generic When with specific logic is hard without context.
    // I will create a specific When for the error cases to be safe, or use a flag.
    // Let's use a simple flag string.

    private String scenarioType = "SUCCESS";

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void setupMandatoryViolation() {
        scenarioType = "MISSING_MANDATORY";
        aggregate = new ScreenMapAggregate(VALID_SCREEN_ID);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void setupLengthViolation() {
        scenarioType = "LENGTH_EXCEEDED";
        aggregate = new ScreenMapAggregate(VALID_SCREEN_ID);
    }

    // Redefine the generic When for these scenarios to dispatch correctly
    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedWithContext() {
        try {
            if ("SUCCESS".equals(scenarioType)) {
                 Map<String, String> inputs = Map.of("USER", "alice", "PASS", "secret123");
                 cmd = new ValidateScreenInputCmd(VALID_SCREEN_ID, inputs, VALID_REQUIRED_FIELDS, VALID_CONSTRAINTS);
            } else if ("MISSING_MANDATORY".equals(scenarioType)) {
                 // Missing 'PASS'
                 Map<String, String> inputs = Map.of("USER", "alice"); 
                 cmd = new ValidateScreenInputCmd(VALID_SCREEN_ID, inputs, VALID_REQUIRED_FIELDS, VALID_CONSTRAINTS);
            } else if ("LENGTH_EXCEEDED".equals(scenarioType)) {
                 // 'USER' > 10 chars
                 Map<String, String> inputs = Map.of("USER", "very_long_name", "PASS", "secret123");
                 cmd = new ValidateScreenInputCmd(VALID_SCREEN_ID, inputs, VALID_REQUIRED_FIELDS, VALID_CONSTRAINTS);
            }
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(caughtException.getMessage().contains("Validation failed"), "Error message should indicate validation failure");
    }
}
