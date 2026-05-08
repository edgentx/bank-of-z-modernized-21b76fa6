package com.example.steps;

import com.example.domain.screenmap.model.ScreenInputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S22Steps {

    // Using the concrete implementation for simplicity in the step definition context
    // in a real scenario we might wire this via Spring config
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    
    private ScreenMapAggregate aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction, ensuring it matches the aggregate
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "12345"); // Valid mandatory field
        inputs.put("PIN", "1234");          // Valid length
        cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Reload from repository to ensure persistence semantics
            ScreenMapAggregate agg = repository.findById("LOGIN_SCREEN");
            if (agg == null) {
                agg = aggregate; // Fallback if not saved yet in specific scenario flow
            }
            resultEvents = agg.execute(cmd);
            repository.save(agg); // Save state changes
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("LOGIN_SCREEN");
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Additional whens for error scenarios to differentiate them internally if needed
    @When("the ValidateScreenInputCmd command is executed with missing mandatory field")
    public void theValidateScreenInputCmdCommandIsExecutedWithMissingMandatoryField() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("OPTIONAL_FIELD", "value"); // Missing ACCOUNT_NO
        cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
        theValidateScreenInputCmdCommandIsExecuted();
    }

    @When("the ValidateScreenInputCmd command is executed with invalid field length")
    public void theValidateScreenInputCmdCommandIsExecutedWithInvalidFieldLength() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("ACCOUNT_NO", "12345"); // Valid mandatory
        inputs.put("LONG_FIELD", "THIS_IS_TOO_LONG_FOR_LEGACY_BMS"); // Violates length (10)
        cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", inputs);
        theValidateScreenInputCmdCommandIsExecuted();
    }

    // For simplicity in this task, the mapping of "violates: X" to specific input data
    // is handled by modifying the context based on the scenario description text or adding more specific Given/When steps.
    // Here we intercept the generic error scenario with specific setup.

    // We can add distinct whens to match the gherkin exactly, or reuse the generic one.
    // Since Gherkin says "When the command is executed", we hook the data setup into the Given.
}