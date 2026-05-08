package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.ScreenInputValidatedEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
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

    // --- Background / Given ---

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // "Valid" usually implies it exists and has rules loaded.
        // We use the default constructor which loads default definitions for the tests.
        aggregate = new ScreenMapAggregate("SCR01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Screen ID is handled in aggregate initialization, but we ensure command matches
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        // Based on default definitions: accountNum(opt), amount(mand), reference(mand)
        inputs.put("amount", "100.00");
        inputs.put("reference", "PAYMENT_001");
        // Optional field
        inputs.put("accountNum", "12345");
        
        cmd = new ValidateScreenInputCmd("SCR01", inputs);
    }

    // --- Success Scenario ---

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
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent, "Event should be ScreenInputValidatedEvent");
        
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", validatedEvent.type());
        assertEquals("SCR01", validatedEvent.aggregateId());
        assertNotNull(validatedEvent.occurredAt());
    }

    // --- Failure Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFieldsMustBeValidated() {
        aggregate = new ScreenMapAggregate("SCR02");
        // Define a mandatory field
        aggregate.defineField("txCode", true, 4);
        
        Map<String, String> inputs = new HashMap<>();
        // Intentionally omit 'txCode'
        inputs.put("optionalField", "value");
        
        cmd = new ValidateScreenInputCmd("SCR02", inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCR03");
        // Define a field with strict length (e.g. BMS 3270 field length 5)
        aggregate.defineField("custId", false, 5);
        
        Map<String, String> inputs = new HashMap<>();
        // Violate length (e.g. "123456" is length 6, max is 5)
        inputs.put("custId", "123456");
        
        cmd = new ValidateScreenInputCmd("SCR03", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain errors typically manifest as IllegalStateException or IllegalArgumentException in this pattern
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
