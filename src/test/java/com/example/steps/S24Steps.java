package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
        // Initialize state to valid defaults if necessary, though constructor handles it
    }

    @And("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Context provided in When block via Command constructor
    }

    @And("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Context provided in When block
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Context provided in When block
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            Command cmd = new UpdateRoutingRuleCmd("route-1", "MODERN", Instant.now().plusSeconds(3600));
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("routing.updated", resultEvents.get(0).type());
    }

    // Scenario 2: Dual Processing Violation
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-bad-1");
        aggregate.markDualProcessingViolation(); // Helper method to set the bad state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException.getMessage().contains("exactly one backend system"));
    }

    // Scenario 3: Versioning Violation
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-bad-2");
        aggregate.markVersioningViolation(); // Helper method to set the bad state
    }

    // Helper In-Memory Repository implementation if not already present in test scope structure
    static class InMemoryLegacyTransactionRouteRepository implements LegacyTransactionRouteRepository {
        private final java.util.Map<String, LegacyTransactionRoute> store = new java.util.HashMap<>();
        @Override public void save(LegacyTransactionRoute aggregate) { store.put(aggregate.id(), aggregate); }
        @Override public Optional<LegacyTransactionRoute> findById(String routeId) { return Optional.ofNullable(store.get(routeId)); }
    }
}
