package com.example.steps;

import com.example.domain.navigation.model.InputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
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

    private ScreenMapAggregate screenMap;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        screenMap = new ScreenMapAggregate("SCREEN_001");
        // Configure: USER_ID is mandatory, max 10 chars. NAME is optional, max 50.
        screenMap.defineField("USER_ID", true, 10);
        screenMap.defineField("NAME", false, 50);
        inputFields = new HashMap<>();
        caughtException = null;
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        screenMap = new ScreenMapAggregate("SCREEN_001");
        screenMap.defineField("USER_ID", true, 10); // Mandatory
        inputFields = new HashMap<>();
        // Leaving inputFields empty to violate the rule
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        screenMap = new ScreenMapAggregate("SCREEN_001");
        screenMap.defineField("USER_ID", true, 5); // Very short constraint
        inputFields = new HashMap<>();
        inputFields.put("USER_ID", "123456"); // Length 6 > 5
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Implicitly handled by the aggregate initialization and command creation
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        inputFields.put("USER_ID", "ALICE");
        inputFields.put("NAME", "Alice In Wonderland");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenMap.id(), inputFields);
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals("SCREEN_001", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("mandatory") || caughtException.getMessage().contains("constraints"));
    }
}
