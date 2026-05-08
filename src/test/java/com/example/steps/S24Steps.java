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

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Given Step 1
    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        String routeId = "route-test-123";
        aggregate = new LegacyTransactionRoute(routeId);
        repository.save(aggregate);
    }

    // Given Step 2
    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Context setup - ruleId is part of the command in the When step
        // We assume valid strings for this step unless checking specific violations
    }

    // Given Step 3
    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context setup - newTarget is part of the command
    }

    // Given Step 4
    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context setup - date is part of the command
    }

    // Negative Scenario Setup 1
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        String routeId = "route-dual-violation";
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.markDualProcessingViolation(); // Sets the internal state to throw exception
        repository.save(aggregate);
    }

    // Negative Scenario Setup 2
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        String routeId = "route-version-violation";
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.markVersioningViolation(); // Sets internal state to throw exception
        repository.save(aggregate);
    }

    // When Step
    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        // We need to reload the aggregate to ensure we are acting on the persisted state
        Optional<LegacyTransactionRoute> optAggregate = repository.findById(aggregate.id());
        assertTrue(optAggregate.isPresent(), "Aggregate should exist in repo");
        
        aggregate = optAggregate.get();
        
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(), 
                "rule-abc", 
                "VForce360", 
                Instant.now()
            );
            resultEvents = aggregate.execute(cmd);
            // Persist the updated aggregate state
            repository.save(aggregate);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        } catch (UnknownCommandException e) {
            fail("Command handler not implemented: " + e.getMessage());
        }
    }

    // Then Step 1 (Success)
    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertEquals("RoutingUpdatedEvent", resultEvents.get(0).type());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    // Then Step 2 (Failure - Dual Processing)
    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(
            capturedException.getMessage().contains("dual-processing") || 
            capturedException.getMessage().contains("versioned"),
            "Exception message should contain invariant violation details: " + capturedException.getMessage()
        );
    }
}
