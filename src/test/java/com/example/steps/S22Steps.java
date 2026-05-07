package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN_LOGIN_01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Id already set in constructor, or passed in Command
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in the When step via Map construction
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        Map<String, String> validInput = new HashMap<>();
        validInput.put("USERID", "TOM");
        validInput.put("PASSWORD", "SECRET");
        
        try {
            Command cmd = new ValidateScreenInputCmd(aggregate.id(), validInput);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Scenario 2: Missing Mandatory Field
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN_TRANSFER_01");
    }

    // Scenario 3: Field Length
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCREEN_TRANSFER_01");
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals(ScreenInputValidatedEvent.class, resultEvents.get(0).getClass());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for either validation exception or unknown command (invariant violation)
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException ||
            caughtException instanceof UnknownCommandException
        );
    }

    // Static inner class for in-memory repo
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final Map<String, ScreenMapAggregate> store = new HashMap<>();
        @Override public ScreenMapAggregate load(String id) { return store.get(id); }
        @Override public void save(ScreenMapAggregate aggregate) { store.put(aggregate.id(), aggregate); }
    }
}
