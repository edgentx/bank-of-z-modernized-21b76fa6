package com.example.steps;

import com.example.domain.screenmap.model.FieldDefinition;
import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Encapsulated in the aggregate setup
        assertNotNull(aggregate.id());
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Configure aggregate to accept any standard field
        Map<String, FieldDefinition> fields = new HashMap<>();
        fields.put("acctNum", new FieldDefinition(true, 10));
        fields.put("amount", new FieldDefinition(true, 5));
        aggregate.configureFields(fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("acctNum", "123456789");
        inputs.put("amount", "1000");

        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCREEN-1", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN-2");
        // Configure fields: 'acctNum' is mandatory
        Map<String, FieldDefinition> fields = new HashMap<>();
        fields.put("acctNum", new FieldDefinition(true, 10));
        aggregate.configureFields(fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedWithMissingMandatoryField() {
        // Intentionally leave 'acctNum' out of inputs
        Map<String, String> inputs = new HashMap<>();
        inputs.put("optionalField", "value");

        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCREEN-2", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException.getMessage().contains("Mandatory field missing"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCREEN-3");
        // Configure field with max length 5
        Map<String, FieldDefinition> fields = new HashMap<>();
        fields.put("shortField", new FieldDefinition(true, 5));
        aggregate.configureFields(fields);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theCommandIsExecutedWithLongField() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("shortField", "TOO_LONG_VALUE"); // length > 5

        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCREEN-3", inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
