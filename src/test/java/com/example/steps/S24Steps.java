package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingUpdatedEvent;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private String routeId = "route-123";
    private String ruleId = "rule-abc";
    private String newTarget = "VForce360";
    private Instant effectiveDate = Instant.now().plusSeconds(3600);
    private int newRuleVersion = 2;
    private Exception capturedException;
    private List events;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute(routeId);
        // Initialize with valid state to ensure the aggregate itself is healthy
        // For this story, we focus on the UpdateRoutingRuleCmd logic
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Rule ID already initialized in constructor
        assertNotNull(ruleId);
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        assertNotNull(newTarget);
        assertFalse(newTarget.isBlank());
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        assertNotNull(effectiveDate);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, effectiveDate, newRuleVersion);
            events = aggregate.execute(cmd);
            // Reload from repository to simulate persistence/retrieval cycle if needed, 
            // though direct aggregate execution is sufficient for unit/domain logic tests.
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) events.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals(routeId, event.aggregateId());
        assertEquals(newTarget, event.newTarget());
        assertEquals(newRuleVersion, event.ruleVersion());
    }

    // Negative Scenarios

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute(routeId);
        // Flip the flag to simulate the invariant violation state
        aggregate.setViolationFlags(true, false); 
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute(routeId);
        // Flip the flag to simulate the invariant violation state
        aggregate.setViolationFlags(false, true);
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // In our domain implementation, invariants throw IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
