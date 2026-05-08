package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import com.example.domain.screenmap.repository.ScreenMapRepository;
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
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private Map<String, String> inputFields;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        // Setup default valid configuration
        aggregate.defineField("USER_ID", 10);
        aggregate.defineField("PASSWORD", 20);
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Implicit in the aggregate setup, but we ensure the input map aligns with the aggregate ID
        if (inputFields == null) inputFields = new HashMap<>();
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        inputFields = new HashMap<>();
        inputFields.put("USER_ID", "validuser");
        inputFields.put("PASSWORD", "password123");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Reload from repo to simulate persistence flow
            var agg = repository.findById("LOGIN_SCREEN").orElseThrow();
            
            // If the scenario setup modified the aggregate rules (mandatory fields), they should be persisted. 
            // For this test, we assume the 'aggregate' variable holds the state we want to test against 
            // if we modified it directly in 'Given'.
            // In a real scenario, we would persist the changes made in the 'Given' steps.
            // Here we just use the instance we have.
            
            var cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputFields);
            resultEvents = agg.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }

    // Scenario 2 Specifics
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        aggregate.defineField("USER_ID", 10);
        aggregate.defineMandatoryField("USER_ID"); // USER_ID is mandatory
        inputFields = new HashMap<>(); // Empty input
        inputFields.put("PASSWORD", "password123"); // Missing USER_ID
    }

    // Scenario 3 Specifics
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        aggregate.defineField("USER_ID", 5); // Max length 5
        inputFields = new HashMap<>();
        inputFields.put("USER_ID", "toolonguser"); // Length 11
    }
}