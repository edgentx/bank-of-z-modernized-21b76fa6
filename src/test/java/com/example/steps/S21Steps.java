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

public class S21Steps {

    // In-memory repository implementation for testing
    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private java.util.Map<String, ScreenMapAggregate> store = new java.util.HashMap<>();
        @Override public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
            return aggregate;
        }
        @Override public Optional<ScreenMapAggregate> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }
        @Override public ScreenMapAggregate create(String id) {
            var agg = new ScreenMapAggregate(id);
            store.put(id, agg);
            return agg;
        }
    }

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = repository.create("test-screen-map-1");
        assertNotNull(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Assuming valid ID is non-null and reasonable length, passed in the cmd later.
        // This step effectively primes the context for the command creation.
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Similar to above.
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command for the happy path
        if (cmd == null) {
            cmd = new RenderScreenCmd("ACCTSUM1", "WEB"); // Valid: len <= 8, len <= 4
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = repository.create("test-map-mandatory");
        // Setting up a command with null/blank fields to trigger violation
        cmd = new RenderScreenCmd(null, "WEB"); // screenId is null
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLengths() {
        aggregate = repository.create("test-map-bms");
        // Setting up a command with screenId > 8 chars
        cmd = new RenderScreenCmd("LONGSCREENID123", "MOBL");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(caughtException.getMessage().contains("mandatory") || caughtException.getMessage().contains("BMS constraints"), 
            "Exception message should indicate validation failure: " + caughtException.getMessage());
    }
}
