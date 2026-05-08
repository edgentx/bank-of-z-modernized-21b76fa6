package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.Command;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentScreenId;
    private Map<String, String> currentInputFields;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        currentScreenId = "TEST_SCREEN_1";
        aggregate = new ScreenMapAggregate(currentScreenId);
        // Setup a mandatory field for the aggregate context
        aggregate.addFieldDefinition("MANDATORY_FIELD", true, 10);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // implicitly currentScreenId
        assertNotNull(currentScreenId);
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // We assume valid means it meets the constraints set in 'aValidScreenMapAggregate'
        currentInputFields = Map.of("MANDATORY_FIELD", "VALUE");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(currentScreenId, currentInputFields);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("input.validated", resultEvents.get(0).type());
    }

    // --- Failure Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        currentScreenId = "MANDATORY_SCREEN";
        aggregate = new ScreenMapAggregate(currentScreenId);
        aggregate.addFieldDefinition("REQUIRED_1", true, 10);
        // Input fields missing the mandatory key
        currentInputFields = Map.of("OPTIONAL_FIELD", "DATA");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        currentScreenId = "LENGTH_SCREEN";
        aggregate = new ScreenMapAggregate(currentScreenId);
        aggregate.addFieldDefinition("SHORT_FIELD", true, 5);
        // Input field value longer than 5
        currentInputFields = Map.of("SHORT_FIELD", "TOO_LONG_DATA");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception but command succeeded");
        // We check for IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
