package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRouteAggregate aggregate;
    private Exception caughtException;
    private EvaluateRoutingCmd command;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRouteAggregate("route-1");
        repository.save(aggregate);
    }

    @And("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        // Command constructed in When step, but we ensure state here
    }

    @And("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        // Command constructed in When step
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        try {
            command = new EvaluateRoutingCmd("route-1", "DEPOSIT", Map.of("amount", 100), 1);
            var events = aggregate.execute(command);
            repository.save(aggregate); // persist state change
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have uncommitted events");
        assertTrue(events.get(0) instanceof RoutingEvaluatedEvent, "Event should be RoutingEvaluatedEvent");

        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) events.get(0);
        assertEquals("routing.evaluated", event.type());
        assertEquals("MODERN", event.targetSystem());
        assertEquals(1, event.ruleVersion());
    }

    // --- Scenarios for Rejections ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRouteAggregate("route-dual");
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRouteAggregate("route-noversion");
        repository.save(aggregate);
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecutedViolating() {
        try {
            // This command setup is generic; the specific violation logic is triggered by the aggregate state
            // or command content based on the scenario.
            if (aggregate.id().equals("route-dual")) {
                // Use a type that maps to null in the simple switch logic to simulate unhandled/ambiguous
                command = new EvaluateRoutingCmd("route-dual", "UNKNOWN_TYPE", Map.of(), 1);
            } else if (aggregate.id().equals("route-noversion")) {
                // Use a null/invalid version
                command = new EvaluateRoutingCmd("route-noversion", "DEPOSIT", Map.of(), null);
            } else {
                // Default happy path command if not caught above (shouldn't happen in these scenarios)
                command = new EvaluateRoutingCmd("route-1", "DEPOSIT", Map.of(), 1);
            }
            aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
