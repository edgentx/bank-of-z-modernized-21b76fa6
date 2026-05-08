package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.Command;
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
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        // Define the screen map rules (BMS constraints)
        aggregate.defineField("USER_ID", 10, true);   // Mandatory, max 10
        aggregate.defineField("PASSWORD", 20, true);  // Mandatory, max 20
        aggregate.defineField("NEW_PIN", 4, false);   // Optional, max 4
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction steps
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "ALICE");
        inputs.put("PASSWORD", "secret123");
        this.cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
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
        
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", validatedEvent.type());
        assertEquals("LOGIN_SCREEN", validatedEvent.aggregateId());
    }

    // Negative Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        aggregate.defineField("USER_ID", 10, true);
        aggregate.defineField("PASSWORD", 20, true);
        
        // Provide inputs missing the mandatory PASSWORD
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "BOB");
        // Password is missing
        
        this.cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengthConstraints() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        aggregate.defineField("USER_ID", 5, true); // Max 5 chars

        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "LONGNAME"); // Length 8 > 5
        
        this.cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertInstanceOf(IllegalStateException.class, caughtException);
        
        // Ensure no events were emitted
        assertNull(resultEvents);
    }
}
