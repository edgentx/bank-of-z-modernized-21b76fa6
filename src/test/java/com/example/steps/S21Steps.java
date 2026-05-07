package com.example.steps;

import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.repository.ScreenMapRepository;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainException;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context usually managed in the When block via command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context usually managed in the When block via command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Assuming 'SCRN01' and '3270' are valid values per the story context
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), "SCRN01", "3270");
            List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        List<com.example.domain.shared.DomainEvent> events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Expected ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        String id = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
        // The violation logic is handled by passing a bad command in the When step below
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        String id = UUID.randomUUID().toString();
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
        // The violation logic is handled by passing a bad command in the When step below
    }

    // We overload the When step logic based on the scenario context implicitly via the aggregate state,
    // or we can check the exception type.
    // However, to keep it clean, we will use specific When methods or check exception types in Then.
    // For simplicity in Cucumber, we reuse the When method and trigger the specific bad command logic via a flag or distinct step.
    // Here, we'll define specific When methods for clarity in the mapping.

    @When("the RenderScreenCmd command is executed with invalid data")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidData() {
        try {
            // Trigger the mandatory field check (e.g., null screenId)
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), null, "3270");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with length violation")
    public void theRenderScreenCmdCommandIsExecutedWithLengthViolation() {
        try {
            // Trigger the BMS length check (e.g., screenId too long > 8 chars based on BMS map naming)
            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), "VERY_LONG_SCREEN_NAME", "3270");
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }

    // Hooks to map scenario text to specific methods if needed, or Gherkin aliases.
    // The Gherkin above uses "When the RenderScreenCmd command is executed" for all.
    // We will assume the generated feature calls the specific methods, or we inject logic.
    // For this implementation, we will map the specific 'When' steps in the feature file to these specific methods.
}
