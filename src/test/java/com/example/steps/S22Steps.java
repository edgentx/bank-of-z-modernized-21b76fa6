package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
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
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction below, using aggregate ID for simplicity
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Setup valid fields based on default empty config
        Map<String, String> fields = new HashMap<>();
        // If we configure fields below, we add them here. For happy path empty map is valid.
        cmd = new ValidateScreenInputCmd(aggregate.id(), fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFieldsMustBeValidatedBeforeScreenSubmission() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
        // Define 'accountNumber' as mandatory
        aggregate.configureField("accountNumber", 10, true);
        
        // Prepare command missing the mandatory field
        Map<String, String> fields = new HashMap<>();
        // fields.put("accountNumber", "12345"); // MISSING
        cmd = new ValidateScreenInputCmd(aggregate.id(), fields);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengthsMustStrictlyAdhereToLegacyBMSConstraintsDuringTheTransitionPeriod() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
        // Define 'accountNumber' with max length 5
        aggregate.configureField("accountNumber", 5, false);
        
        // Prepare command with value length 10
        Map<String, String> fields = new HashMap<>();
        fields.put("accountNumber", "1234567890"); // Too long
        cmd = new ValidateScreenInputCmd(aggregate.id(), fields);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
