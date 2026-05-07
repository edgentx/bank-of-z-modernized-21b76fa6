package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
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
    private Exception caughtException;

    // Standard BMS constraints for test setup
    private final String SCREEN_ID = "BANKMENU1";
    private final Map<String, Integer> DEFINITIONS = Map.of(
        "ACCOUNT_NUM", 12,
        "TRANSACTION_TYPE", 4,
        "AMOUNT", 16
    );
    private final String MANDATORY_FIELD = "ACCOUNT_NUM";

n    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Setup aggregate with standard constraints for this story
        aggregate = new ScreenMapAggregate(SCREEN_ID, DEFINITIONS, Set.of(MANDATORY_FIELD));
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Command is built in the 'When' step, this confirms the state context
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Command is built in the 'When' step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        // Construct a valid command based on the aggregate's constraints
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "12345678"); // Valid length, mandatory
        inputs.put("TRANSACTION_TYPE", "TRF"); // Valid length

        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", validatedEvent.type());
        assertEquals(SCREEN_ID, validatedEvent.screenId());
        assertNotNull(validatedEvent.occurredAt());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFieldsMustBeValidatedBeforeScreenSubmission() {
        aggregate = new ScreenMapAggregate(SCREEN_ID, DEFINITIONS, Set.of(MANDATORY_FIELD));
        caughtException = null;
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted_MandatoryCheck() {
        // Construct command MISSING the mandatory field
        Map<String, String> inputs = new HashMap<>();
        inputs.put("TRANSACTION_TYPE", "TRF"); // Missing ACCOUNT_NUM

        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("All mandatory input fields"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengthsMustStrictlyAdhereToLegacyBMSConstraintsDuringTheTransitionPeriod() {
        aggregate = new ScreenMapAggregate(SCREEN_ID, DEFINITIONS, Set.of(MANDATORY_FIELD));
        caughtException = null;
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted_LengthCheck() {
        // Construct command with EXCESSIVE length for ACCOUNT_NUM (Max 12)
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "1234567890123"); // Length 13, exceeds 12

        this.cmd = new ValidateScreenInputCmd(SCREEN_ID, inputs);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }
}
