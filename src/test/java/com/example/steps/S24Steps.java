package com.example.steps;

import com.example.domain.legacybridge.command.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.event.RoutingUpdatedEvent;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    // Test Doubles
    public static class TestRepository implements LegacyTransactionRouteRepository {
        private LegacyTransactionRoute aggregate;
        @Override
        public void save(LegacyTransactionRoute aggregate) { this.aggregate = aggregate; }
        @Override
        public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(aggregate); }
    }

    private final TestRepository repository = new TestRepository();
    private LegacyTransactionRoute aggregate;
    private UpdateRoutingRuleCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Rule ID is handled in command construction below
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Target handled in command construction
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Date handled in command construction
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // If command wasn't pre-built by specific scenario steps, build defaults
            if (command == null) {
                command = new UpdateRoutingRuleCmd("route-1", "rule-1", "VForce360", Instant.now(), 2);
            }
            resultEvents = aggregate.execute(command);
            // Persist side-effect
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent, "Event should be RoutingUpdatedEvent");

        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("route-1", event.aggregateId());
        assertEquals(2, event.newRuleVersion());
        assertEquals("VForce360", event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesSingleTarget() {
        aggregate = new LegacyTransactionRoute("route-1");
        aggregate.markDualProcessingViolation();
        command = new UpdateRoutingRuleCmd("route-1", "rule-1", "DUAL_TARGET", Instant.now(), 2);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-1");
        aggregate.markVersioningViolation();
        command = new UpdateRoutingRuleCmd("route-1", "rule-1", "VForce360", Instant.now(), 0);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
