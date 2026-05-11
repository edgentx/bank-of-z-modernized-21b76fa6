package com.example.steps;

import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMap aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception caughtException;
    private ValidateScreenInputCmd command;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMap("LOGIN_SCREEN");
        // Configure some legacy BMS fields
        aggregate.defineField("USER_ID", 10, true);  // Mandatory, max 10 chars
        aggregate.defineField("PASSWORD", 20, true); // Mandatory, max 20 chars
        aggregate.defineField("OPTION", 1, false);   // Optional, max 1 char
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction below
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Valid input setup for success scenario
        this.command = new ValidateScreenInputCmd(
            "LOGIN_SCREEN",
            Map.of("USER_ID", "admin", "PASSWORD", "secret")
        );
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aValidScreenMapAggregate();
        // Missing mandatory USER_ID
        this.command = new ValidateScreenInputCmd(
            "LOGIN_SCREEN",
            Map.of("PASSWORD", "secret") // USER_ID missing
        );
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aValidScreenMapAggregate();
        // USER_ID is 11 chars (max 10)
        this.command = new ValidateScreenInputCmd(
            "LOGIN_SCREEN",
            Map.of("USER_ID", "administrator", "PASSWORD", "secret")
        );
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            var loadedAggregate = repository.findById("LOGIN_SCREEN");
            if (loadedAggregate == null) throw new IllegalStateException("Aggregate not found");
            loadedAggregate.execute(command);
            repository.save(loadedAggregate);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        var events = repository.findById("LOGIN_SCREEN").uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertEquals("input.validated", events.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
