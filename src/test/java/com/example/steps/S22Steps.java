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
        aggregate = new ScreenMapAggregate("SCRN01");
        // Define a standard layout
        aggregate.defineField("ACCOUNT_NUM", 10, false);
        aggregate.defineField("TRANS_AMT", 12, false);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "1234567890");
        inputs.put("TRANS_AMT", "100.00");
        this.cmd = new ValidateScreenInputCmd("SCRN01", inputs);
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
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertInstanceOf(ScreenInputValidatedEvent.class, event);
        ScreenInputValidatedEvent sve = (ScreenInputValidatedEvent) event;
        assertEquals("input.validated", sve.type());
        assertEquals("SCRN01", sve.aggregateId());
        assertNotNull(sve.occurredAt());
    }

    // --- Scenarios for Rejection ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Define a mandatory field
        aggregate.defineField("REF_NUM", 10, true); // Mandatory

        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NUM", "000");
        // Missing REF_NUM
        this.cmd = new ValidateScreenInputCmd("SCRN01", inputs);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengths() {
        aggregate = new ScreenMapAggregate("SCRN01");
        // Define a strict legacy length (e.g. 5 bytes)
        aggregate.defineField("SHORT_CODE", 5, false);

        Map<String, String> inputs = new HashMap<>();
        inputs.put("SHORT_CODE", "123456"); // Length 6 > 5
        this.cmd = new ValidateScreenInputCmd("SCRN01", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertInstanceOf(IllegalArgumentException.class, caughtException);
        // Verify the message contains domain logic context
        assertTrue(caughtException.getMessage().contains("Field") || 
                   caughtException.getMessage().contains("Mandatory"));
    }

    // Setup for the valid scenario to wire it all together explicitly if needed
    @And("a valid inputFields is provided")
    public void setupValidInputs() {
        // No op, already done in previous step, or reset here
    }
}
