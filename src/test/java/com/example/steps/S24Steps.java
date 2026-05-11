package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // In-memory repo implementation for testing
    private final LegacyTransactionRouteRepository repo = new LegacyTransactionRouteRepository() {
        private LegacyTransactionRoute stored;
        @Override
        public void save(LegacyTransactionRoute agg) { this.stored = agg; }
        @Override
        public Optional<LegacyTransactionRoute> findById(String id) {
            if (stored != null && stored.id().equals(id)) return Optional.of(stored);
            if (aggregate != null && aggregate.id().equals(id)) return Optional.of(aggregate);
            return Optional.empty();
        }
    };

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-1");
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Context setup, usually stored in a context object, but here implicit
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context setup
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context setup
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd("route-1", "rule-101", "MODERN", Instant.now(), 1);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("route-1", event.aggregateId());
        assertEquals("MODERN", event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-bad-1");
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-bad-2");
        this.aggregate.markVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Domain errors in this context are RuntimeExceptions
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
