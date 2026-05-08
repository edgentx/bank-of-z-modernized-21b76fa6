package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.ScreenInputValidatedEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private final Map<String, String> inputFields = new HashMap<>();

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("LOGIN_SCR_01");
        aggregate.defineField("USER_ID", true, 10);
        aggregate.defineField("PASSWORD", true, 20);
        aggregate.defineField("OPTION_1", false, 1);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in context, assuming the command uses the aggregate ID
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        inputFields.put("USER_ID", "ALICE");
        inputFields.put("PASSWORD", "secret");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("LOGIN_SCR_01");
        aggregate.defineField("USER_ID", true, 10);
        aggregate.defineField("PASSWORD", true, 20);
        
        // Intentionally omitting PASSWORD
        inputFields.put("USER_ID", "ALICE");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLengthConstraints() {
        aggregate = new ScreenMapAggregate("LOGIN_SCR_01");
        aggregate.defineField("USER_ID", true, 10); // Max 10
        aggregate.defineField("PASSWORD", true, 20);

        inputFields.put("USER_ID", "VERY_LONG_USERNAME_EXCEEDS_LIMIT");
        inputFields.put("PASSWORD", "secret");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        cmd = new ValidateScreenInputCmd(aggregate.id(), inputFields);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
