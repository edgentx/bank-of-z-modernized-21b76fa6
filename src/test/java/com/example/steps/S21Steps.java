package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd.
 */
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // In-Memory Repository Implementation for testing
    private static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private final java.util.Map<String, ScreenMapAggregate> store = new java.util.HashMap<>();
        @Override public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
            return aggregate;
        }
        @Override public Optional<ScreenMapAggregate> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-01");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in the execution step for brevity
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in the execution step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid execution
        theRenderScreenCmdCommandIsExecutedWith("SCR01", "DESKTOP");
    }

    @When("the RenderScreenCmd command is executed with screenId {string} and deviceType {string}")
    public void theRenderScreenCmdCommandIsExecutedWith(String screenId, String deviceType) {
        try {
            // Reload from repo to simulate persistence
            aggregate = repository.findById("map-01").orElseThrow();
            RenderScreenCmd cmd = new RenderScreenCmd("map-01", screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("map-01", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("map-invalid-mand");
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("map-invalid-len");
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidData() {
        // Handles the specific violations based on context from the setup
        // We'll use a specific long string for the length test
        theRenderScreenCmdCommandIsExecutedWith("TOO_LONG_SCREEN_ID_FOR_BMS", "TERMINAL_3270");
    }

    @When("the RenderScreenCmd command is executed with null data")
    public void theRenderScreenCmdCommandIsExecutedWithNullData() {
        theRenderScreenCmdCommandIsExecutedWith(null, null);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Mapping for specific violations
    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedForViolation() {
        // Logic router for the violation scenarios
        if (aggregate.id().equals("map-invalid-mand")) {
            theRenderScreenCmdCommandIsExecutedWithNullData();
        } else if (aggregate.id().equals("map-invalid-len")) {
            theRenderScreenCmdCommandIsExecutedWithInvalidData();
        }
    }
}
