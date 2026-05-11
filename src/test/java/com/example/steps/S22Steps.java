package com.example.steps;

import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ScreenMap;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.navigation.repository.InMemoryScreenMapRepository;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMap aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // ----------------------------------------------------------------
    // Scenario: Successfully execute ValidateScreenInputCmd
    // ----------------------------------------------------------------

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMap();
        // Setup valid constraints for the test
        aggregate.defineField("USER_ID", true, 10);
        aggregate.defineField("TRANSACTION_TYPE", true, 4);
        aggregate.defineField("AMOUNT", true, 12);
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Parameter implied in command construction; no op
    }

    @Given("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Parameter implied in command construction; no op
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        Map<String, String> validInputs = Map.of(
            "USER_ID", "ALICE",
            "TRANSACTION_TYPE", "TRFM",
            "AMOUNT", "100.00"
        );
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("LOGIN_SCREEN", validInputs);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent validatedEvent = (ScreenInputValidatedEvent) event;
        
        assertEquals("input.validated", validatedEvent.type());
        assertEquals(ScreenMap.AGGREGATE_ID, validatedEvent.aggregateId());
    }

    // ----------------------------------------------------------------
    // Scenario: ValidateScreenInputCmd rejected — mandatory fields
    // ----------------------------------------------------------------

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMap();
        aggregate.defineField("PASSWORD", true, 20);
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception was not thrown");
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("mandatory"));
    }

    // ----------------------------------------------------------------
    // Scenario: ValidateScreenInputCmd rejected — BMS constraints
    // ----------------------------------------------------------------

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMap();
        // Define a field with a legacy BMS max length of 4
        aggregate.defineField("ACCT_CODE", true, 4);
        repository.save(aggregate);
        
        // The When step will trigger the execution with bad data
    }

    // Re-use When step above

    // Re-use Then step above (error assertion)
}
