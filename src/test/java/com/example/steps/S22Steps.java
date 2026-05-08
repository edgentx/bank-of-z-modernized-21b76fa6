package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN_001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // The ID is implicitly handled by the aggregate creation, but we ensure command matches.
        // Handled in the command creation step below.
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        // ACCOUNT_NUMBER is mandatory, max 12
        inputs.put("ACCOUNT_NUMBER", "123456789");
        // TRANSACTION_AMOUNT max 10
        inputs.put("TRANSACTION_AMOUNT", "100.00");
        this.command = new ValidateScreenInputCmd(aggregate.id(), inputs);
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
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
        assertEquals(command.inputFields(), event.inputFields());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN_002");
        Map<String, String> inputs = new HashMap<>();
        // Missing ACCOUNT_NUMBER (mandatory)
        inputs.put("TRANSACTION_AMOUNT", "100.00");
        this.command = new ValidateScreenInputCmd(aggregate.id(), inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCREEN_003");
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUMBER", "123456789");
        // TRANSACTION_AMOUNT max is 10. Providing 11.
        inputs.put("TRANSACTION_AMOUNT", "12345678901"); 
        this.command = new ValidateScreenInputCmd(aggregate.id(), inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        
        // Verify message contains context about the error
        assertTrue(caughtException.getMessage().contains("mandatory") || caughtException.getMessage().contains("BMS constraints"));
    }
}