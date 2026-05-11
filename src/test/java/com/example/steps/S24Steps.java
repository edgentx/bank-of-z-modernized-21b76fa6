package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class S24Steps {

    // In-Memory Repository Implementation for Test Scope
    public static class InMemoryRepository implements LegacyTransactionRouteRepository {
        private final Map<String, LegacyTransactionRoute> store = new HashMap<>();
        @Override
        public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override
        public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
    }

    private final InMemoryRepository repository = new InMemoryRepository();
    private LegacyTransactionRoute aggregate;
    private Throwable caughtException;
    private RoutingUpdatedEvent resultingEvent;

    // Test Data Builders
    private String ruleId = "RULE-101";
    private String newTarget = "VForce360";
    private Instant effectiveDate = Instant.now();
    private int newRuleVersion = 2;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-legacy-1");
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // ruleId defaults to "RULE-101"
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // newTarget defaults to "VForce360"
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // effectiveDate defaults to Instant.now()
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-dual-violation");
        // Use the test hook to force the aggregate into a state that will fail the invariant
        aggregate.markDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-version-violation");
        // Use the test hook to force the aggregate into a state that will fail the invariant
        aggregate.markVersioningViolation();
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(),
                ruleId,
                newTarget,
                effectiveDate,
                newRuleVersion
            );
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = (RoutingUpdatedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvent, "Expected RoutingUpdatedEvent to be emitted");
        Assertions.assertEquals("routing.updated", resultingEvent.type());
        Assertions.assertEquals(aggregate.id(), resultingEvent.aggregateId());
        Assertions.assertEquals(newTarget, resultingEvent.targetSystem());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Invariant violations throw IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected IllegalStateException or IllegalArgumentException, but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
