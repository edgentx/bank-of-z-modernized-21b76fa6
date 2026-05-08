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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Using the default constructor which sets up standard constraints
        aggregate = new ScreenMapAggregate("SCREEN_01");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN_01");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("SCREEN_01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction below
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = Map.of(
            "ACCOUNT_NUM", "123456789",
            "TRANS_CODE", "TX01",
            "AMOUNT", "100.00"
        );
        cmd = new ValidateScreenInputCmd("SCREEN_01", inputs);
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
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals("SCREEN_01", event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        
        // Specific check for the mandatory fields error message
        if (thrownException.getMessage().contains("mandatory")) {
             assertTrue(thrownException.getMessage().contains("All mandatory input fields must be validated"));
        }
        // Specific check for the BMS constraints error message
        else if (thrownException.getMessage().contains("lengths must strictly adhere")) {
             assertTrue(thrownException.getMessage().contains("Field lengths must strictly adhere to legacy BMS constraints"));
        }
    }
    
    // Additional wiring for violation scenarios to set specific bad inputs
    @And("the inputFields are missing mandatory data")
    public void theInputFieldsAreMissingMandatoryData() {
        // Missing ACCOUNT_NUM
        Map<String, String> inputs = Map.of(
            "TRANS_CODE", "TX01"
        );
        cmd = new ValidateScreenInputCmd("SCREEN_01", inputs);
    }

    @And("the inputFields exceed BMS length constraints")
    public void theInputFieldsExceedBmsLengthConstraints() {
        // ACCOUNT_NUM is constrained to 12 chars in the aggregate default constructor
        Map<String, String> inputs = Map.of(
            "ACCOUNT_NUM", "1234567890123", // 13 chars
            "TRANS_CODE", "TX01"
        );
        cmd = new ValidateScreenInputCmd("SCREEN_01", inputs);
    }
}
