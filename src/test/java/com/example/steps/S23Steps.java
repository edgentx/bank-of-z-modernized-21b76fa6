package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-23 (EvaluateRoutingCmd).
 * Note: Located in src/test/java per standard Maven structure, 
 though Feature Feedback mentions tests/ directory (directory structure adjustment would be env-specific).
 */
public class S23Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute EvaluateRoutingCmd
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
        aggregate.configure(1, false); // Version 1, no dual-write
        repository.save(aggregate);
    }

    @And("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // Implicitly handled in When step, but we ensure state is valid
    }

    @And("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Implicitly handled in When step
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("route-1", "TRANSFER", Map.of("amount", 100), 1);
        try {
            // Reload aggregate to simulate persistence
            var agg = repository.findById("route-1").orElseThrow();
            resultEvents = agg.execute(cmd);
            repository.save(agg); // Save changes (events applied internally)
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        assertEquals("routing.evaluated", event.type());
    }

    // Scenario: EvaluateRoutingCmd rejected — Dual-processing
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_write() {
        aggregate = new LegacyTransactionRoute("route-2");
        // Configure to violate invariant: Dual write enabled
        aggregate.configure(1, true); 
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("exactly one backend system"));
    }

    // Scenario: EvaluateRoutingCmd rejected — Versioning
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-3");
        // Configure aggregate to be at version 1
        aggregate.configure(1, false);
        repository.save(aggregate);
    }

    @When("the EvaluateRoutingCmd command is executed with invalid version")
    public void the_evaluate_routing_cmd_command_is_executed_invalid_version() {
        // Attempt to evaluate against version 2, but aggregate is at 1
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("route-3", "TRANSFER", Map.of(), 2);
        try {
            var agg = repository.findById("route-3").orElseThrow();
            resultEvents = agg.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}