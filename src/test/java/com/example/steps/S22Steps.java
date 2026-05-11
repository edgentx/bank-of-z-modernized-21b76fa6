package com.example.steps;

import com.example.domain.routing.model.ScreenInputValidatedEvent;
import com.example.domain.routing.model.ScreenMapAggregate;
import com.example.domain.routing.model.ValidateScreenInputCmd;
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

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        aggregate.defineField("ACCOUNT", true, 10);
        aggregate.defineField("TX_AMOUNT", true, 12);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in the 'When' step construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in the 'When' step construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT", "1234567890");
        inputs.put("TX_AMOUNT", "100.00");
        
        cmd = new ValidateScreenInputCmd("screen-1", inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertInstanceOf(ScreenInputValidatedEvent.class, event);
        assertEquals("input.validated", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("screen-map-2");
        aggregate.defineField("ACCOUNT", true, 10); // Mandatory
        aggregate.defineField("REF", false, 5);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedWithMissingMandatory() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("REF", "A1"); // ACCOUNT is missing
        
        cmd = new ValidateScreenInputCmd("screen-2", inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertInstanceOf(IllegalArgumentException.class, caughtException);
        assertTrue(caughtException.getMessage().contains("mandatory"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-3");
        aggregate.defineField("ACCOUNT", true, 5); // Max length 5
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedWithInvalidLength() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT", "1234567890"); // Length 10 > Max 5
        
        cmd = new ValidateScreenInputCmd("screen-3", inputs);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }
}
