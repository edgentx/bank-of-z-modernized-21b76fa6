package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainException;
import com.example.domain.legacybridge.command.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.event.RoutingUpdatedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private Exception caughtException;
    private String aggregateId;

    // Test Data
    private String ruleId = "RULE-101";
    private String newTarget = "MODERN";
    private Instant effectiveDate = Instant.now();

    // Repository implementation for this test
    private static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String id) { return Optional.ofNullable(store.get(id)); }
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        this.aggregateId = "route-" + UUID.randomUUID();
        this.aggregate = new LegacyTransactionRoute(aggregateId);
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        this.ruleId = "RULE-" + System.currentTimeMillis();
    }

    @Given("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        this.newTarget = "VForce360";
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        this.effectiveDate = Instant.now().plusSeconds(60);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        this.aggregateId = "route-dual-" + UUID.randomUUID();
        this.aggregate = new LegacyTransactionRoute(aggregateId);
        // Violation: Mark as dual processing
        aggregate.markAsDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        this.aggregateId = "route-vers-" + UUID.randomUUID();
        this.aggregate = new LegacyTransactionRoute(aggregateId);
        // Violation: Mark as versioning violation
        aggregate.markAsVersioningViolation();
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        // Reload to ensure we have a clean state or simulate fetching from DB
        LegacyTransactionRoute agg = repository.findById(aggregateId).orElseThrow();
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(ruleId, newTarget, effectiveDate);

        try {
            agg.execute(cmd);
            repository.save(agg); // persist state change
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        LegacyTransactionRoute agg = repository.findById(aggregateId).orElseThrow();
        Assertions.assertFalse(agg.getUncommittedEvents().isEmpty());
        
        DomainEvent event = agg.getUncommittedEvents().get(0);
        Assertions.assertTrue(event instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent rue = (RoutingUpdatedEvent) event;
        Assertions.assertEquals("routing.updated", rue.type());
        Assertions.assertEquals(aggregateId, rue.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // In DDD, domain errors are often specific exceptions or IllegalArgumentException/IllegalStateException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}