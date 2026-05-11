package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.ScreenInputValidatedEvent;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ValidateScreenInputCmd;
import com.example.domain.uimodel.repository.ScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    @Autowired
    private ScreenMapRepository repository;

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SOME_SCREEN");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in command construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Default valid command
            if (cmd == null) {
                cmd = new ValidateScreenInputCmd("SOME_SCREEN", 
                    Map.of("ACCT_NUM", "12345", "TRANS_AMT", "100.00"));
            }
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        DomainEvent event = aggregate.uncommittedEvents().get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
        assertEquals("input.validated", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SOME_SCREEN");
        repository.save(aggregate);
        // Create command missing 'ACCT_NUM'
        cmd = new ValidateScreenInputCmd("SOME_SCREEN", Map.of("TRANS_AMT", "100.00"));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException, "Should be IllegalStateException");
        assertTrue(caughtException.getMessage().contains("mandatory") 
                   || caughtException.getMessage().contains("BMS"), 
                   "Error message should mention the constraint violation");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SOME_SCREEN");
        repository.save(aggregate);
        // ACCT_NUM max is 10, providing 11
        cmd = new ValidateScreenInputCmd("SOME_SCREEN", 
            Map.of("ACCT_NUM", "12345678901", "TRANS_AMT", "100.00"));
    }
}
