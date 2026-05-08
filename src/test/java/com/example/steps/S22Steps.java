package com.example.steps;

import com.example.domain.screen.model.ScreenInputValidatedEvent;
import com.example.domain.screen.model.ScreenMap;
import com.example.domain.screen.model.ValidateScreenInputCmd;
import com.example.domain.screen.repository.InMemoryScreenMapRepository;
import com.example.domain.screen.repository.ScreenMapRepository;
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

    // Test State
    private ScreenMap aggregate;
    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Standard Screen Map Setup
    private final String SCREEN_ID = "LOGIN01";

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMap(SCREEN_ID);
        // Setup fields: ACCT_NO (len 10, mandatory), TRANS_AMT (len 12, mandatory), REF_CODE (len 20, optional)
        aggregate.defineField("ACCT_NO", 10, true);
        aggregate.defineField("TRANS_AMT", 12, true);
        aggregate.defineField("REF_CODE", 20, false);
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aValidScreenMapAggregate();
        // Scenario setup: we will create a command missing mandatory fields in the 'When' step logic usually,
        // but here we prepare the aggregate. The violation comes from the input data in the 'When'.
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aValidScreenMapAggregate();
        // Constraint violation will be triggered by input data in 'When' step.
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Implicitly used when constructing the command in the When step
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Prepare valid data map
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCT_NO", "12345");
        inputs.put("TRANS_AMT", "100.00");
        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // If the specific scenario setup didn't initialize the command, assume a default valid one
            // but typically Scenarios dictate specific invalid data sets.
            // However, for the negative scenarios, we need to inject bad data here 
            // based on the Scenario title context or previous steps.
            
            // Heuristic: If cmd is null, we are likely in a negative test scenario requiring setup.
            // Since Cucumber steps are distinct, we rely on the scenario flow.
            // For simplicity in this implementation:
            // 1. Positive scenario: cmd is set in 'And valid inputFields'
            // 2. Negative scenarios: we construct invalid commands here if cmd is null.
            
            if (cmd == null) {
                 Map<String, String> inputs = new HashMap<>();
                 // Default valid fields
                 inputs.put("ACCT_NO", "12345");
                 inputs.put("TRANS_AMT", "100.00");
                 
                 // Heuristic check for scenario context to fail validation
                 // (In a real framework, we'd have specific Given steps for the data)
                 // Since the Gherkin provided is generic, we assume the 'Given' 
                 // prepared the Aggregate state, and here we just trigger a command 
                 // that might be valid. 
                 // To satisfy the specific rejection scenarios, we'll assume the 
                 // 'And valid inputFields' step was SKIPPED for negative scenarios, 
                 // leaving cmd null. 
                 
                 // Let's assume the default is valid for the positive case, 
                 // and we check for specific exception context for negative cases.
                 this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);
            }
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Specific "When" logic overrides for negative scenarios if needed
    // Note: In standard Cucumber, the 'When' step is often unique or overloaded.
    // Here we use a single When method for simplicity, relying on state.
    
    // We need a way to inject the bad data for the negative scenarios.
    // We'll add helper Given steps or conditional logic.
    // Based on the Gherkin provided, the negative scenarios don't have an "And valid inputFields" step.
    
    @Given("inputFields missing mandatory ACCT_NO")
    public void inputFieldsMissingMandatory() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("TRANS_AMT", "100.00");
        // Missing ACCT_NO
        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);
    }

    @Given("inputFields where ACCT_NO exceeds length")
    public void inputFieldsExceedLength() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCT_NO", "1234567890123"); // Length 13, Max 10
        inputs.put("TRANS_AMT", "100.00");
        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);
    }
    
    // We need to update the Gherkin mapping to use these given steps, 
    // or handle the negative cases purely in 'When'. 
    // The prompt Gherkin is:
    // "Given a ScreenMap aggregate that violates... When Validate... Then rejected"
    // It implies the Command provided in the When is invalid. 
    // I will intercept the execution for the negative scenarios by checking 
    // the state of the aggregate or just overriding the cmd creation for those specific tests. 
    
    // RE-READING ACCEPTANCE CRITERIA:
    // Scenario 2 & 3 DO NOT have the "And a valid inputFields is provided" step.
    // So in 'When', cmd will be null. 
    // I will initialize a DEFAULT valid command if null, but that passes the first test.
    // To fail the specific tests, I should define specific invalid inputs for those contexts.
    // However, without unique step names, I have to be clever.
    // I will map the "Given... violates..." to setup the invalid command directly.

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void setupMandatoryViolation() {
        aValidScreenMapAggregate();
        // Force the next command to be invalid
        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, Map.of("TRANS_AMT", "100")); // Missing ACCT_NO
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void setupLengthViolation() {
        aValidScreenMapAggregate();
        // Force the next command to be invalid
        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, Map.of(
            "ACCT_NO", "12345", 
            "TRANS_AMT", "100",
            "REF_CODE", "THIS_STRING_IS_TOO_LONG_FOR_BMS_CONSTRAINT"
        ));
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Domain logic throws IllegalStateException for invariants
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
