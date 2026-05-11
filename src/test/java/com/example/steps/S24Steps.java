package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RuleUpdatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryS24Repository();
    private LegacyTransactionRoute aggregate;
    private Exception caughtException;
    private List<?> resultingEvents;

    // Mock Repository implementation
    private static class InMemoryS24Repository implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-1");
        // Ensure clean state
        this.aggregate.clearEvents(); 
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // No-op, command will use hardcoded valid ID
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // No-op, command will use hardcoded valid target
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // No-op, command will use hardcoded valid date
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-dual-violation");
        // Use the helper method defined in the aggregate to simulate the violation state
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-version-violation");
        // Use the helper method defined in the aggregate to simulate the violation state
        this.aggregate.markVersioningViolation();
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            aggregate.id(),
            "RULE-100",
            "VForce360",
            Instant.now().plusSeconds(3600),
            1 // Valid version
        );
        
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultingEvents, "Expected events to be returned");
        Assertions.assertFalse(resultingEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultingEvents.get(0) instanceof RuleUpdatedEvent, "Expected RuleUpdatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for the specific error messages defined in the aggregate
        String msg = caughtException.getMessage();
        Assertions.assertTrue(
            msg.contains("dual-processing") || msg.contains("versioned") || msg.contains("rollback"),
            "Expected specific domain error message, but got: " + msg
        );
    }
}
