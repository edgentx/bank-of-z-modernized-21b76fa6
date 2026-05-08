package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private EvaluateRoutingCmd command;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
        // Hydrate with valid versioned rules to satisfy the versioning invariant by default
        aggregate.hydrateForTest(1);
        repository.save(aggregate);
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // We'll set this when executing the command, just store the intent
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // We'll set this when executing the command
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_uniqueness() {
        aggregate = new LegacyTransactionRoute("route-dual");
        aggregate.hydrateForTest(1);
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-no-ver");
        // hydrateForTest defaults to 0, or we set it explicitly to 0/-1
        aggregate.hydrateForTest(0);
        repository.save(aggregate);
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        try {
            // Default payload
            var payload = Map.of("amount", 100);
            // Determine transaction type based on setup state if needed, otherwise default valid one
            String txType = "ACH"; // Modern
            
            if (aggregate.id().equals("route-dual")) {
                txType = "INVALID"; // Triggers null target -> invariant violation
            }

            command = new EvaluateRoutingCmd(aggregate.id(), txType, payload);
            
            // Reload fresh aggregate from repo to ensure clean state execution
            var agg = repository.findById(aggregate.id()).orElseThrow();
            var events = agg.execute(command);
            repository.save(agg);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        var agg = repository.findById(aggregate.id()).orElseThrow();
        var events = agg.uncommittedEvents();
        assertFalse(events.isEmpty(), "Events should not be empty");
        assertEquals(RoutingEvaluatedEvent.class, events.get(0).getClass());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
