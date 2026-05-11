package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.UnknownCommandException;
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
    private String testRouteId = "test-route-24";

    // Helper to load or create aggregate for the scenario
    private void loadAggregate() {
        Optional<LegacyTransactionRoute> loaded = repository.findById(testRouteId);
        if (loaded.isEmpty()) {
            aggregate = new LegacyTransactionRoute(testRouteId);
        } else {
            aggregate = loaded.get();
        }
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRoute(testRouteId);
        // Evaluate routing first to ensure it's in a valid initialized state
        aggregate.execute(new com.example.domain.legacybridge.model.EvaluateRoutingCmd(
            testRouteId, 
            java.util.Map.of("initial", true), 
            1
        ));
        repository.save(aggregate);
        loadAggregate();
    }

    @Given("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // Context: The command will be created in the 'When' step using this context
        // Implicitly handled by the test flow construction
    }

    @And("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // Context: The command will be created in the 'When' step
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // Context: The command will be created in the 'When' step
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_single_backend_processing() {
        aggregate = new LegacyTransactionRoute(testRouteId);
        aggregate.markDualProcessingViolation();
        repository.save(aggregate);
        loadAggregate();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_routing_rules_versioning() {
        aggregate = new LegacyTransactionRoute(testRouteId);
        aggregate.markVersioningViolation();
        repository.save(aggregate);
        loadAggregate();
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            // Command parameters valid for the happy path; violations are triggered by aggregate state
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "rule-123", 
                "MODERN", 
                Instant.now().plusSeconds(3600)
            );
            aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNotNull(aggregate.uncommittedEvents(), "Events list should not be null");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "At least one event should be emitted");
        
        Assertions.assertTrue(
            aggregate.uncommittedEvents().stream().anyMatch(e -> e instanceof RoutingUpdatedEvent),
            "Expected a RoutingUpdatedEvent"
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Invariants typically throw IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected a domain error (IllegalArgumentException or IllegalStateException)"
        );
    }

    // Inner class for In-Memory Repository if not present in mocks package or referenced from there
    public static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
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
}