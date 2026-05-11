package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Logic handled in the next step for object construction simplicity
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Map matching the aggregate's expected mandatory fields (ACCOUNT_NUMBER, TRANSACTION_AMOUNT)
        this.cmd = new ValidateScreenInputCmd(
            "LOGIN_SCREEN",
            Map.of(
                "ACCOUNT_NUMBER", "123456789", // Valid length <= 10
                "TRANSACTION_AMOUNT", "100.00"  // Valid length <= 12
            )
        );
    }

    // Scenario 2: Mandatory Field Violation
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        // Missing ACCOUNT_NUMBER
        this.cmd = new ValidateScreenInputCmd(
            "LOGIN_SCREEN",
            Map.of("TRANSACTION_AMOUNT", "100.00")
        );
    }

    // Scenario 3: Length Violation
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        // ACCOUNT_NUMBER defined as max 10, providing 15
        this.cmd = new ValidateScreenInputCmd(
            "LOGIN_SCREEN",
            Map.of(
                "ACCOUNT_NUMBER", "123456789012345", // Length 15 > 10
                "TRANSACTION_AMOUNT", "100.00"
            )
        );
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof InputValidatedEvent, "Event should be instance of InputValidatedEvent");
        assertEquals("input.validated", event.type());
        assertEquals("screen-map-1", event.aggregateId());
        
        // Verify specific content (optional but good practice)
        InputValidatedEvent validatedEvent = (InputValidatedEvent) event;
        assertEquals("LOGIN_SCREEN", validatedEvent.screenId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }
}
