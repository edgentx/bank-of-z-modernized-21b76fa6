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

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Using "LOGIN" which is defined in the Aggregate's static map
        this.screenId = "LOGIN";
        this.aggregate = new ScreenMapAggregate(screenId);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.screenId = "LOGIN";
        this.aggregate = new ScreenMapAggregate(screenId);
        // Setup input that violates the rule (missing USER_ID)
        this.inputFields = Map.of("PASSWORD", "secret");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLengthConstraints() {
        this.screenId = "LOGIN";
        this.aggregate = new ScreenMapAggregate(screenId);
        // Setup input that violates the rule (USER_ID > 10 chars)
        // The definition in Aggregate has USER_ID max length 10
        this.inputFields = Map.of(
            "USER_ID", "very-long-user-id-exceeds-limit",
            "PASSWORD", "secret"
        );
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId is set in the 'Given' steps
        assertNotNull(screenId);
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Valid data for LOGIN screen
        this.inputFields = Map.of(
            "USER_ID", "admin",
            "PASSWORD", "password123"
        );
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenId, inputFields);
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
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(screenId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Domain logic exceptions are modeled as RuntimeExceptions in this context
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        assertFalse(aggregate.isInputValidated());
    }
}
