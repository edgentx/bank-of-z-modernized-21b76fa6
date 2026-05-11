package com.example.steps;

import com.example.domain.routing.model.ScreenMapAggregate;
import com.example.domain.routing.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cucumber Steps for S-22: ValidateScreenInputCmd.
 * Uses an in-memory aggregate for validation logic testing.
 */
public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCRN001");
        // Define a sample field map
        // simulate repository hydration
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in context setup or specific scenario setup
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in scenario context
    }

    // Scenario 1: Success
    @Given("a valid inputFields is provided")
    public void setupValidInputFields() {
        // We configure the aggregate to expect a field 'AMOUNT' with max 10, mandatory
        aggregate.defineField("AMOUNT", 10, true);
        Map<String, String> inputs = new HashMap<>();
        inputs.put("AMOUNT", "100.00");
        this.command = new ValidateScreenInputCmd("SCRN001", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("input.validated", resultEvents.get(0).type());
    }

    // Scenario 2: Mandatory Field Rejection
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCRN002");
        aggregate.defineField("CUST_NAME", 30, true); // Mandatory
        
        // Submit missing field
        Map<String, String> inputs = new HashMap<>(); // Empty input
        this.command = new ValidateScreenInputCmd("SCRN002", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException.getMessage().contains("Validation failed"));
    }

    // Scenario 3: BMS Length Constraints
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("SCRN003");
        aggregate.defineField("REF_NUM", 5, false); // Max 5 chars
        
        Map<String, String> inputs = new HashMap<>();
        inputs.put("REF_NUM", "123456"); // 6 chars
        this.command = new ValidateScreenInputCmd("SCRN003", inputs);
    }

}
