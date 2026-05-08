package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Optional;

public class S24Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private Exception capturedException;
    private String ruleId = "RULE-123";
    private String newTarget = "MODERN";
    private Instant effectiveDate = Instant.now();

    // Repository implementation for this test
    private static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("ROUTE-1");
        repository.save(aggregate);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Defaults are set in field declaration
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Defaults are set in field declaration
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Defaults are set in field declaration
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        // Reload to ensure we are testing against persisted state if needed, or use instance
        var agg = repository.findById("ROUTE-1").orElseThrow();
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(ruleId, newTarget, effectiveDate);
        try {
            var events = agg.execute(cmd);
            repository.save(agg); // Persist state changes
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        var agg = repository.findById("ROUTE-1").orElseThrow();
        // Assuming aggregate exposes events via getter from AggregateRoot or similar
        // In strict DDD, we inspect uncommitted events, here we check state or list
        // For this test, we check uncommitted events size and type if accessible
        // If not, we rely on the aggregate's internal state verification logic.
        // Let's check the uncommitted events list which is typically accessible or we check behavior.
        // Since we don't have a getter in the stub, we check that no exception was thrown.
        // A more robust test would mock the event publishing, but here we verify no error.
        Assertions.assertFalse(agg.uncommittedEvents().isEmpty());
        Assertions.assertTrue(agg.uncommittedEvents().get(0) instanceof RoutingUpdatedEvent);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("ROUTE-DUAL");
        // Setup state to cause failure
        // e.g. mark it as already evaluated or set a specific internal flag
        // Based on the existing legacybridge model, we might use a setter or specific command history
        // Since we can't change the constructor easily in this context, we assume a helper exists.
        // Or we use the helper `markDualProcessingViolation` if it was in the legacy package, 
        // but here we are in legacybridge. Let's assume the aggregate tracks state.
        // To trigger the dual processing error in `execute`, we might need to simulate a state 
        // where a target is already active. 
        // For this test, we will rely on the aggregate's internal logic.
        // If the aggregate needs a specific setup, we do it here.
        // (The provided legacybridge class had `markDualProcessingViolation`, assuming that pattern holds)
        // However, I will implement the aggregate logic to trigger based on the *Command* content
        // or Aggregate state. Let's assume Aggregate state 'alreadyRouted'.
        // Since I cannot edit the existing LegacyTransactionRoute class to add methods, 
        // I will assume the failure condition is triggered by the command data or a specific constructor pattern.
        // WAIT: The prompt says "Implement the UpdateRoutingRuleCmd command on the LegacyTransactionRoute aggregate".
        // I MUST provide the Aggregate code. So I can control the invariants.
        // I will set a flag in the test setup if the aggregate allows it, or pass a specific command.
        // The scenario says "Given an aggregate that violates...". 
        // Let's use a setter if I write it, or an internal state.
        // I will assume the aggregate has a `forceViolation` flag for testing or similar.
        // Actually, I'll provide the code, so I can just implement `setDualProcessingViolation(true)`.
        // Assuming I add that method to the Aggregate I'm providing.
        // But wait, I shouldn't add methods to existing aggregates if forbidden.
        // I'll check the constraints: "Implement the UpdateRoutingRuleCmd command on the LegacyTransactionRoute aggregate."
        // This implies I am writing the logic for this command *inside* the existing aggregate structure.
        // Since I can't see the full source of `LegacyTransactionRoute` in `legacybridge` other than the snippet provided,
        // and the snippet shows `markDualProcessingViolation` method, I will use that.
        // NOTE: The snippet provided in the prompt for `legacybridge` DOES have `markDualProcessingViolation`.
        this.aggregate.markDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("ROUTE-VER");
        // Snippet shows `markVersioningViolation`
        this.aggregate.markVersioningViolation();
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Check for specific error types if necessary (IllegalArgumentException vs IllegalStateException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}