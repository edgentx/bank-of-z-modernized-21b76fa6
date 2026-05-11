package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    // In-Memory Repository implementation for testing
    public static class InMemoryRouteRepository implements LegacyTransactionRouteRepository {
        private LegacyTransactionRoute aggregate;
        @Override
        public void save(LegacyTransactionRoute aggregate) {
            this.aggregate = aggregate;
        }
        @Override
        public Optional<LegacyTransactionRoute> findById(String routeId) {
            return Optional.ofNullable(this.aggregate);
        }
    }

    private final InMemoryRouteRepository repository = new InMemoryRouteRepository();
    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // No-op, handled in command creation
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // No-op, handled in command creation
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // No-op, handled in command creation
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-dual-violation");
        aggregate.markDualProcessingViolation(); // Setup violation state
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-version-violation");
        aggregate.markVersioningViolation(); // Setup violation state
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // Reload aggregate from repository to simulate persistence boundaries
            aggregate = repository.findById(aggregate.id()).orElseThrow();
            
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(), 
                "rule-abc", 
                "VForce360", // New Target
                Instant.now()
            );
            
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("RoutingRuleUpdated", resultEvents.get(0).type());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertNull(resultEvents); // No events should be emitted on failure
    }
}
