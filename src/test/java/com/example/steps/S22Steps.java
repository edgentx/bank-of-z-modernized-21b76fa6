package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
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
    private Map<String, String> inputFields = new HashMap<>();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN_001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled implicitly by using 'SCREEN_001' in the aggregate constructor
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Configure aggregate expectations
        aggregate.configureField("ACCOUNT_NUM", 10, 10, true);
        aggregate.configureField("AMOUNT", 12, 1, true);

        // Provide valid input
        inputFields.put("ACCOUNT_NUM", "1234567890");
        inputFields.put("AMOUNT", "100.00");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("SCREEN_001", inputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    }

    // --- Negative Scenario: Mandatory Fields ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN_002");
        aggregate.configureField("REQUIRED_FIELD", 10, 1, true);

        // Intentionally leave out the required field
        inputFields.put("OPTIONAL_FIELD", "data");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("mandatory"));
    }

    // --- Negative Scenario: Field Lengths ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCREEN_003");
        aggregate.configureField("SHORT_FIELD", 5, 1, false);

        // Provide input that exceeds the legacy BMS constraint
        inputFields.put("SHORT_FIELD", "TOO_LONG_DATA");
    }

}
