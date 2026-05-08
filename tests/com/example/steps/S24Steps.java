package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingRuleUpdatedEvent;
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

    // Test Doubles
    static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();

        @Override
        public void save(LegacyTransactionRoute aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public Optional<LegacyTransactionRoute> findById(String routeId) {
            return Optional.ofNullable(store.get(routeId));
        }
    }

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private UpdateRoutingRuleCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute UpdateRoutingRuleCmd

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
        repository.save(aggregate);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Rule ID is part of command construction, captured in When step
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Target is part of command construction
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Date is part of command construction
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        // Initialize command with valid defaults for the success scenario
        if (command == null) {
            command = new UpdateRoutingRuleCmd(
                    "route-123",
                    "rule-abc",
                    "VForce360", // Valid target
                    2,           // Valid version
                    Instant.now()
            );
        }
        try {
            // Reload aggregate to simulate persistence fetch
            LegacyTransactionRoute agg = repository.findById("route-123").orElseThrow();
            resultEvents = agg.execute(command);
            repository.save(agg); // Save modified state
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingRuleUpdatedEvent);

        RoutingRuleUpdatedEvent event = (RoutingRuleUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("route-123", event.aggregateId());
        assertEquals("VForce360", event.newTarget());
    }

    // Scenario: UpdateRoutingRuleCmd rejected — Dual-processing

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-dual");
        aggregate.markUnstableForDualProcessingViolation();
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed for dual processing")
    public void theUpdateRoutingRuleCmdCommandIsExecutedForDualProcessing() {
        // The command itself is valid, but the aggregate logic or specific command params trigger the error.
        // Based on the code implementation, passing "DUAL" as target triggers the error.
        command = new UpdateRoutingRuleCmd(
                "route-dual",
                "rule-bad",
                "DUAL", // Triggers violation in aggregate logic
                1,
                Instant.now()
        );
        try {
            LegacyTransactionRoute agg = repository.findById("route-dual").orElseThrow();
            resultEvents = agg.execute(command);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    // Scenario: UpdateRoutingRuleCmd rejected — Versioning

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-version");
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed for versioning")
    public void theUpdateRoutingRuleCmdCommandIsExecutedForVersioning() {
        // Trigger versioning error via invalid version in command
        command = new UpdateRoutingRuleCmd(
                "route-version",
                "rule-ver",
                "CICS",
                0, // Invalid version
                Instant.now()
        );
        try {
            LegacyTransactionRoute agg = repository.findById("route-version").orElseThrow();
            resultEvents = agg.execute(command);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("prevent dual-processing") ||
                caughtException.getMessage().contains("versioned to allow safe rollback"));
    }
}
