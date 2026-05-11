package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.InputValidatedEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ValidateScreenInputCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
        aggregate.initialize();
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in context construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Context prepared for When
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        // Default happy path inputs for valid scenario
        executeCommand(Map.of("ACCT", "1234567890"));
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals(InputValidatedEvent.class, resultEvents.get(0).getClass());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
        aggregate.initialize();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsLength() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
        aggregate.initialize();
    }

    @When("the ValidateScreenInputCmd command is executed with empty mandatory field")
    public void theValidateScreenInputCmdCommandIsExecutedWithMissingField() {
        // ACCT is mandatory but empty
        executeCommand(Map.of("ACCT", ""));
    }

    @When("the ValidateScreenInputCmd command is executed with oversized field")
    public void theValidateScreenInputCmdCommandIsExecutedWithOversizedField() {
        // ACCT max length 10, providing 11
        executeCommand(Map.of("ACCT", "12345678901"));
    }

    // Helper to centralize execution logic
    private void executeCommand(Map<String, String> inputs) {
        try {
            Command cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // In a real app we might check specific exception types or messages
        // e.g., Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Cucumber will map the "When" lines in the negative scenarios to these specific methods
    // based on the text matching. To ensure mapping works perfectly with the shared step text:
    
    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecutedNegative() {
       // This generic hook delegates to specific violations if needed, 
       // but Cucumber matches specific text first. 
       // The scenarios above use unique text to disambiguate.
       // For the scenarios with exact matching text in feature file:
       // "When the ValidateScreenInputCmd command is executed"
       // We need to map context.
       
       // Actually, looking at the scenarios:
       // 1. "When the ValidateScreenInputCmd command is executed" (Positive)
       // 2. "When the ValidateScreenInputCmd command is executed" (Negative - Mandatory)
       // 3. "When the ValidateScreenInputCmd command is executed" (Negative - Length)
       // 
       // Since the text is identical, we use the "Given" to set state, and we need a single "When"
       // that behaves differently based on state? 
       // No, BDD steps usually need distinct text OR we combine logic.
       // The feature file provided has identical text. I will update the step definition logic
       // to handle the execution, but since the step text is identical, I will use the same method.
       // However, the specific 'Given' setup determines which inputs to use.
    }

    // Refining the execution flow: The Given steps set up the aggregate.
    // The When steps invoke the command. 
    // Because the text is identical, I will use a catch-all When method,
    // but I need to know *what* to test.
    // To keep it simple and working: The test class state setup in 'Given' will determine inputs.
    
    private Map<String, String> testInputs;

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void setupViolatesMandatory() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
        aggregate.initialize();
        this.testInputs = Map.of("ACCT", ""); // Violation
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void setupViolatesBms() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
        aggregate.initialize();
        this.testInputs = Map.of("ACCT", "TOO_LONG_ACCT"); // Violation
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void executeCommandFromContext() {
        if (this.testInputs == null) {
             // Default happy path
             this.testInputs = Map.of("ACCT", "1234567890");
        }
        try {
            Command cmd = new ValidateScreenInputCmd(aggregate.id(), this.testInputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        } finally {
            this.testInputs = null; // Reset
        }
    }

}
