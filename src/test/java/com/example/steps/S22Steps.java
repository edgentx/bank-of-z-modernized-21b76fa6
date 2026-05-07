package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private Exception caughtException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("DEPOSIT_SCREEN_01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup handled in When construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Context setup handled in When construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        // Default success case data
        Map<String, String> inputs = Map.of(
            "TRANSACTION_AMT", "100.00", 
            "ACCOUNT_NUM", "123456789"
        );
        this.cmd = new ValidateScreenInputCmd("DEPOSIT_SCREEN_01", inputs);
        executeCommand();
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        Assertions.assertEquals("input.validated", event.type());
        Assertions.assertEquals("DEPOSIT_SCREEN_01", event.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("DEPOSIT_SCREEN_01");
        // The violation comes from the input data provided in the 'When' step below
    }

    // We can reuse the generic When or create specific ones based on context.
    // Cucumber will match the first pattern it finds, or specific ones.
    // Given the strict phrasing in the feature file, we need a specific When or logic inside.
    // Let's use the specific When handler for the error cases to differentiate the data.

    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void theValidateScreenInputCmdCommandIsExecutedWithMissingFields() {
        // Missing TRANSACTION_AMT
        this.cmd = new ValidateScreenInputCmd("DEPOSIT_SCREEN_01", Map.of("SOME_FIELD", "value"));
        executeCommand();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("DEPOSIT_SCREEN_01");
    }

    @When("the ValidateScreenInputCmd command is executed with excessive field length")
    public void theValidateScreenInputCmdCommandIsExecutedWithExcessiveLength() {
        // ACCOUNT_NUM > 10 chars
        this.cmd = new ValidateScreenInputCmd("DEPOSIT_SCREEN_01", Map.of(
            "TRANSACTION_AMT", "100.00",
            "ACCOUNT_NUM", "1234567890123" // Too long
        ));
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
        // Optional: verify message content
        // Assertions.assertTrue(caughtException.getMessage().contains("..."));
    }

    private void executeCommand() {
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
}