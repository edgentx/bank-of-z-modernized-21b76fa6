package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
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

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Using ID "LOGIN_SCR_01" which implies a login screen context
        aggregate = new ScreenMapAggregate("LOGIN_SCR_01");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMandatoryViolation() {
        aggregate = new ScreenMapAggregate("LOGIN_SCR_01");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithLengthViolation() {
        aggregate = new ScreenMapAggregate("LOGIN_SCR_01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled implicitly in command construction below
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "ALICE");
        inputs.put("PASSWORD", "secret123");
        this.cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            if (this.cmd == null) {
                // If cmd wasn't explicitly set for negative cases, create a default one
                // This relies on the specific violation context established in the Given steps
                // However, for clarity, we usually set specific inputs in the step implementation
                // but here we assume the command was built or we build a default to trigger failure
                // Ideally, we pass specific data.
                // For the negative scenarios, we will construct the command inline below in extended steps or checks.
                // But to support the current structure:
                throw new IllegalStateException("Command not initialized for scenario context");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the ValidateScreenInputCmd command is executed with missing mandatory fields")
    public void executeWithMissingMandatory() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "BOB");
        // PASSWORD is missing
        this.cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }

    @When("the ValidateScreenInputCmd command is executed with excessive length fields")
    public void executeWithExcessiveLength() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "ALICE");
        inputs.put("PASSWORD", "secret123");
        inputs.put("TX_AMOUNT", "1234567890123"); // Max is 12, this is 13
        this.cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
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
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("Validation failed"));
    }
}
