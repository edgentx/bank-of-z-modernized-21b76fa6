package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd command;
    private Exception capturedException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("S-22-TEST-SCREEN");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Command constructed in 'When' clause
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Input constructed in 'When' clause
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        Map<String, String> inputs = Map.of(
                "USER_ID", "valid_user",
                "PASSWORD", "secret",
                "OPTION", "1"
        );
        command = new ValidateScreenInputCmd(aggregate.id(), inputs);
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent sve = (ScreenInputValidatedEvent) event;
        
        assertEquals("input.validated", sve.type());
        assertEquals(aggregate.id(), sve.aggregateId());
        assertNotNull(sve.occurredAt());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("S-22-TEST-SCREEN");
        // Intentionally not populating all mandatory fields in the next step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void executeWithMissingMandatory() {
        // Missing PASSWORD
        Map<String, String> inputs = Map.of("USER_ID", "valid_user");
        command = new ValidateScreenInputCmd(aggregate.id(), inputs);

        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLength() {
        this.aggregate = new ScreenMapAggregate("S-22-TEST-SCREEN");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void executeWithInvalidLength() {
        // Create a value longer than 80 chars (BMS constraint)
        String longString = "a".repeat(100);
        Map<String, String> inputs = Map.of(
                "USER_ID", "valid_user",
                "PASSWORD", "secret",
                "OPTION", longString
        );
        command = new ValidateScreenInputCmd(aggregate.id(), inputs);

        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("Mandatory") || capturedException.getMessage().contains("length"));
        assertNull(resultEvents, "No events should be emitted on validation failure");
    }
}
