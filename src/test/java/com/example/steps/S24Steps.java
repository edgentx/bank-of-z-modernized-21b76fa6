package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class S24Steps {

    // This scenario focuses on the aggregate's behavior via Execute(cmd).
    // While the aggregate field 'targetSystem' usually implies routing *direction*,
    // we interpret the state invariants provided in the existing class.
    // Existing class has: dualProcessingViolation, versioningViolation.
    
    private LegacyTransactionRoute aggregate;
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private Exception capturedException;
    private RoutingUpdatedEvent lastEvent;

    static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
        private final Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Rule ID is encapsulated in the command constructor, used in the 'When' step
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Target is encapsulated in the command constructor
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Date is encapsulated in the command constructor
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            // Construct valid command defaults for the happy path
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "route-1",
                "RULE-101",
                "MODERN",
                Instant.now().plusSeconds(3600)
            );
            
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (RoutingUpdatedEvent) events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNotNull(lastEvent, "Expected a RoutingUpdatedEvent to be emitted");
        Assertions.assertEquals("routing.updated", lastEvent.type());
    }

    // --- Negative Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-violation-1");
        // Using the helper method defined in the existing aggregate class for testing invariants
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-violation-2");
        // Using the helper method defined in the existing aggregate class
        aggregate.markVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Checking for standard domain error types or illegal state exceptions
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}