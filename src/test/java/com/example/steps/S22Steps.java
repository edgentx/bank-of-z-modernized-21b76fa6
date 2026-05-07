package com.example.steps;

import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ValidateScreenInputCmd;
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
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // Standard setup for a valid aggregate
    private void setupValidAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        var definitions = new HashMap<String, ScreenMapAggregate.FieldDefinition>();
        definitions.put("accountNum", new ScreenMapAggregate.FieldDefinition(true, 10));
        definitions.put("amount", new ScreenMapAggregate.FieldDefinition(true, 12));
        definitions.put("reference", new ScreenMapAggregate.FieldDefinition(false, 20));
        aggregate.initialize("LOGIN_SCREEN", definitions);
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        setupValidAggregate();
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // The command will be constructed in the 'When' clause, 
        // but we verify context implies usage of the initialized aggregate's ID.
        assertNotNull(aggregate);
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Pre-verification logic handled in the When/Then flow usually, 
        // or we construct valid inputs here if needed for specific flow.
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        // Default valid inputs for the happy path scenario
        if (cmd == null) {
            var inputs = new HashMap<String, String>();
            inputs.put("accountNum", "1234567890");
            inputs.put("amount", "100.00");
            inputs.put("reference", "Payment");
            cmd = new ValidateScreenInputCmd(aggregate.id(), "LOGIN_SCREEN", inputs);
        }

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("input.validated", resultingEvents.get(0).type());
    }

    // -----------------------------
    // Negative Scenarios
    // -----------------------------

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        setupValidAggregate();
        var inputs = new HashMap<String, String>();
        // Missing 'accountNum' which is mandatory
        inputs.put("amount", "100.00"); 
        cmd = new ValidateScreenInputCmd(aggregate.id(), "LOGIN_SCREEN", inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        setupValidAggregate();
        var inputs = new HashMap<String, String>();
        inputs.put("accountNum", "1234567890");
        // Exceeds length of 12 defined in setup
        inputs.put("amount", "1000000000000000.00"); 
        cmd = new ValidateScreenInputCmd(aggregate.id(), "LOGIN_SCREEN", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertNotNull(resultingEvents);
        assertTrue(resultingEvents.isEmpty(), "No events should be emitted when command is rejected");
    }
}
