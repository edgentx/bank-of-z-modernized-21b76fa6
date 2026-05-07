package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.ScreenFieldDefinition;
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
    private Exception thrownException;

    // --- Scenario 1: Success ---

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
        
        // Configure a valid map with one mandatory field and one optional field
        Map<String, ScreenFieldDefinition> defs = new HashMap<>();
        defs.put("ACCOUNT_NUM", new ScreenFieldDefinition("ACCOUNT_NUM", 10, true));
        defs.put("AMOUNT", new ScreenFieldDefinition("AMOUNT", 12, false));
        aggregate.configureFieldDefinitions(defs);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId is part of the command, handled in the next step or here implicitly
        // We construct the command fully in the next step for simplicity, or store parts here.
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "1234567890"); // Valid length, mandatory provided
        inputs.put("AMOUNT", "100.00");
        
        cmd = new ValidateScreenInputCmd("SCREEN-001", "LOGIN_SCREEN", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    // --- Scenario 2: Mandatory Field Violation ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN-002");
        Map<String, ScreenFieldDefinition> defs = new HashMap<>();
        defs.put("REF_ID", new ScreenFieldDefinition("REF_ID", 15, true)); // Mandatory
        aggregate.configureFieldDefinitions(defs);

        // Missing REF_ID
        Map<String, String> inputs = new HashMap<>(); 
        cmd = new ValidateScreenInputCmd("SCREEN-002", "SUBMIT_SCREEN", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Checking for specific message or type allows precise validation
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("Mandatory field"));
    }

    // --- Scenario 3: Field Length Violation ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCREEN-003");
        Map<String, ScreenFieldDefinition> defs = new HashMap<>();
        defs.put("SHORT_CODE", new ScreenFieldDefinition("SHORT_CODE", 5, false)); // Max 5 chars
        aggregate.configureFieldDefinitions(defs);

        Map<String, String> inputs = new HashMap<>();
        inputs.put("SHORT_CODE", "123456"); // Length 6 > 5
        cmd = new ValidateScreenInputCmd("SCREEN-003", "DATA_ENTRY", inputs);
    }
}
