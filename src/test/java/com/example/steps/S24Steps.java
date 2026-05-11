package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    // Using an in-memory repository for test isolation, as per guidelines.
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepositoryS24();
    
    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // --- Givens ---

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        String routeId = "route-1";
        // Simulating a fresh aggregate
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.hydrate(); // Initialize base state if needed
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Context setup - the ruleId will be constructed in the When step
        // This step is a no-op but exists to satisfy BDD readability
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context setup
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context setup
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        String routeId = "route-dual-violation";
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.markDualProcessingViolation(); // Test hook to simulate the invariant breach
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        String routeId = "route-version-violation";
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.markVersioningViolation(); // Test hook to simulate the invariant breach
        repository.save(aggregate);
    }

    // --- Whens ---

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // Retrieve the aggregate setup in the Given steps
            // In a real scenario, we might load by ID, but here we hold the reference
            // If the reference wasn't set (implicitly in Given), we create a default one
            if (aggregate == null) {
                aggregate = new LegacyTransactionRoute("default-route");
                repository.save(aggregate);
            }

            // Construct command with valid defaults
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(),
                "RULE-101", // valid ruleId
                "MODERN",   // valid newTarget
                Instant.now().plusSeconds(3600) // valid effectiveDate
            );

            resultingEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Save state after execution
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Thens ---

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events should not be null");
        assertFalse(resultingEvents.isEmpty(), "At least one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof RoutingUpdatedEvent, "Event should be RoutingUpdatedEvent");
        
        RoutingUpdatedEvent routingEvent = (RoutingUpdatedEvent) event;
        assertEquals("routing.updated", routingEvent.type());
        assertEquals(aggregate.id(), routingEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // We expect an IllegalStateException or IllegalArgumentException based on the invariant
        assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected a domain rule violation exception, got: " + thrownException.getClass().getSimpleName()
        );
    }

    // Inner helper class to implement the in-memory repo for this specific test class
    // to avoid conflicts with other step definitions if they use different mock implementations.
    private static class InMemoryLegacyTransactionRouteRepositoryS24 implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();

        @Override
        public void save(LegacyTransactionRoute aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public java.util.Optional<LegacyTransactionRoute> findById(String routeId) {
            return java.util.Optional.ofNullable(store.get(routeId));
        }
    }
}
