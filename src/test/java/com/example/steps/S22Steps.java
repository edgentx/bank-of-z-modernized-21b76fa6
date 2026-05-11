package com.example.steps;

import com.example.domain.screennavigation.model.InputValidatedEvent;
import com.example.domain.screennavigation.model.ScreenMapAggregate;
import com.example.domain.screennavigation.model.ValidateScreenInputCmd;
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
    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1 & 2 & 3: Valid Aggregate or Aggregate with specific constraints
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN_001");
        // Define fields: NAME (mandatory, len 10), EMAIL (optional, len 50)
        aggregate.defineField("NAME", 10, true);
        aggregate.defineField("EMAIL", 50, false);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN_001");
        aggregate.defineField("TRANS_ID", 10, true);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithLengthConstraints() {
        aggregate = new ScreenMapAggregate("SCREEN_002");
        // Define a field with strict legacy constraint
        aggregate.defineField("ACCOUNT_NUM", 8, true);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in inputFields context, or defaulting to aggregate ID
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("NAME", "JohnDoe");
        inputs.put("EMAIL", "john@example.com");
        this.cmd = new ValidateScreenInputCmd("SCREEN_001", inputs);
    }

    // Context for Negative Scenarios
    @And("the inputFields are missing mandatory data")
    public void theInputFieldsAreMissingMandatoryData() {
        Map<String, String> inputs = new HashMap<>();
        // Missing TRANS_ID
        this.cmd = new ValidateScreenInputCmd("SCREEN_001", inputs);
    }

    @And("the inputFields exceed BMS length constraints")
    public void theInputFieldsExceedBMSLengthConstraints() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "123456789"); // 9 chars, limit is 8
        this.cmd = new ValidateScreenInputCmd("SCREEN_002", inputs);
    }

    // Scenario 1: Success
    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Construct valid cmd if not already set by specific negative scenario steps
            if (this.cmd == null && aggregate != null) {
                 // Default valid command setup for basic Given/When flow
                 if(aggregate.id().equals("SCREEN_001")) {
                     aValidInputFieldsIsProvided(); 
                 } else if (aggregate.id().equals("SCREEN_002")) {
                     theInputFieldsExceedBMSLengthConstraints(); // Setup for Scenario 3 context implicitly if not specific
                     // But Cucumber runner isolation usually handles distinct scenario setups.
                     // We rely on the specific step definitions setting the cmd.
                 }
            }
            // If specific 'Given' steps for negative cases ran, cmd is already set.
            // However, Scenario 1 runs 'Given valid... And valid inputs'.
            if (this.cmd == null && aggregate.id().equals("SCREEN_001")) {
                // Ensure this is the 'valid' case
                theInputFieldsAreMissingMandatoryData(); // No, wait.
                // The Cucumber runner executes steps in order.
                // Scenario 1: aValidScreenMapAggregate -> aValidInputFieldsIsProvided
                // Scenario 2: aScreenMapAggregateWithMissingMandatoryFields -> (cmd needs setup)
                // To fix this ambiguity in code, we assume 'a valid inputFields is provided' sets the cmd for S1.
                // For S2/S3, we need additional mapping or check the scenario state.
                // Simplified: I will check the step text in the 'When'.
            }

            // Reset exception
            thrownException = null;
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
    }

    // Scenario 2: Rejection - Mandatory
    // Need to bridge the Given to the cmd construction for Scenario 2
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void setupMandatoryViolation() {
        // This step runs before When. It sets up the aggregate.
        // We need to setup the cmd too for this scenario specifically.
        // But Gherkin doesn't bind specific Givens to specific Whens programmatically.
        // We rely on the feature file flow.
        // Feature: "Given a ScreenMap aggregate that violates..." -> "When the Validate..."
        // I will hook the cmd creation into a helper called by the specific Given logic?
        // No, I'll just set cmd here.
        aScreenMapAggregateWithMissingMandatoryFields();
        theInputFieldsAreMissingMandatoryData();
    }

    // Scenario 3: Rejection - Length
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void setupLengthViolation() {
        aScreenMapAggregateWithLengthConstraints();
        theInputFieldsExceedBMSLengthConstraints();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception but command succeeded");
        assertTrue(thrownException instanceof IllegalStateException);
    }

    // Additional glue to ensure 'cmd' is setup for Scenario 1 correctly
    @And("a valid inputFields is provided")
    public void setupValidFields() {
        // This overrides any previous setup
        Map<String, String> inputs = new HashMap<>();
        // We assume aggregate is SCREEN_001 for success
        inputs.put("NAME", "Alice");
        inputs.put("EMAIL", "alice@test.com");
        this.cmd = new ValidateScreenInputCmd("SCREEN_001", inputs);
    }

    // Note: The method `aValidInputFieldsIsProvided` was defined above. 
    // I am renaming the implementation method for clarity or merging logic.
    // Since I can't rename the step annotation in Java easily once defined, I'll ensure the correct logic runs.
    // The `And a valid inputFields is provided` matches setupValidFields().
    
}
