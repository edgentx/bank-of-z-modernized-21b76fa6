package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class S24Steps {

    // In-memory Repository implementation specific to this test class to avoid pollution
    static class LocalInMemoryRepository implements LegacyTransactionRouteRepository {
        private final Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
        public void clear() { store.clear(); }
    }

    private final LocalInMemoryRepository repository = new LocalInMemoryRepository();
    private LegacyTransactionRoute aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    private static final String ROUTE_ID = "test-route-24";

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        repository.clear();
        aggregate = new LegacyTransactionRoute(ROUTE_ID);
        // Initialize with a routing evaluation to simulate a valid state
        EvaluateRoutingCmd evalCmd = new EvaluateRoutingCmd(ROUTE_ID, Map.of("source", "ATM"), 1);
        aggregate.execute(evalCmd);
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        repository.clear();
        aggregate = new LegacyTransactionRoute(ROUTE_ID);
        aggregate.markDualProcessingViolation();
        // Pre-populate repo
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        repository.clear();
        aggregate = new LegacyTransactionRoute(ROUTE_ID);
        aggregate.markVersioningViolation();
        // Pre-populate repo
        repository.save(aggregate);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // No-op, just indicating we will use valid data in the When step
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // No-op
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // No-op
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // Reload aggregate to simulate fetch
            LegacyTransactionRoute agg = repository.findById(ROUTE_ID)
                    .orElseThrow(() -> new IllegalStateException("Aggregate not found"));

            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                    ROUTE_ID,
                    "RULE-101",
                    "MODERN",
                    Instant.now().plusSeconds(60)
            );

            resultEvents = agg.execute(cmd);
            repository.save(agg);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertEquals("routing.updated", resultEvents.get(0).type());
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // We check for IllegalStateException as per the aggregate logic
        Assertions.assertTrue(capturedException instanceof IllegalStateException, 
            "Expected IllegalStateException, got " + capturedException.getClass().getSimpleName());
    }
}
