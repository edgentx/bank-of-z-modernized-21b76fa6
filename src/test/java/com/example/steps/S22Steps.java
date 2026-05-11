package com.example.steps;

import com.example.domain.routing.model.InputValidatedEvent;
import com.example.domain.routing.model.ScreenMapAggregate;
import com.example.domain.routing.model.ValidateScreenInputCmd;
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
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Using 'LOGIN' as the valid screen ID as it maps to the hardcoded definitions in the Aggregate
        aggregate = new ScreenMapAggregate("LOGIN");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Defer full command creation until 'And a valid inputFields is provided'
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Construct the command here assuming screenId matches the aggregate
        cmd = new ValidateScreenInputCmd("LOGIN", Map.of("USER", "admin", "PASS", "secret"));
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("LOGIN");
        // Provide input missing the mandatory 'PASS' field
        cmd = new ValidateScreenInputCmd("LOGIN", Map.of("USER", "admin"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("LOGIN");
        // 'USER' has maxLength 10. Providing 11 chars.
        cmd = new ValidateScreenInputCmd("LOGIN", Map.of("USER", "administrator", "PASS", "secret"));
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
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent, "Event should be InputValidatedEvent");
        
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("LOGIN", event.screenId());
        assertEquals("input.validated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertTrue(caughtException.getMessage().contains("Validation failed"), 
            "Exception message should contain validation details: " + caughtException.getMessage());
    }
}
