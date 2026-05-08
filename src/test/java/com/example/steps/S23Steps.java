package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRouteAggregate aggregate;
    private Throwable thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = repository.create("route-123");
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // State prepared for the command execution
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // State prepared for the command execution
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        var cmd = new EvaluateRoutingCmd("route-123", "WIRE", "{}", "MODERN", 1);
        try {
            aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        var event = events.get(0);
        assertTrue(event instanceof RoutingEvaluatedEvent, "Event should be RoutingEvaluatedEvent");
        assertEquals("routing.evaluated", event.type());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = repository.create("route-dual");
    }

    @When("the EvaluateRoutingCmd command is executed with invalid dual routing")
    public void the_evaluate_routing_cmd_command_is_executed_with_invalid_dual_routing() {
        // Simulate the violation via the command hint
        var cmd = new EvaluateRoutingCmd("route-dual", "WIRE", "{}", "DUAL", 1);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException, "Should be an IllegalStateException");
        assertTrue(thrownException.getMessage().contains("exactly one backend system"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = repository.create("route-no-ver");
    }

    @When("the EvaluateRoutingCmd command is executed with invalid versioning")
    public void the_evaluate_routing_cmd_command_is_executed_with_invalid_versioning() {
        // version 0 or negative violates the rule
        var cmd = new EvaluateRoutingCmd("route-no-ver", "WIRE", "{}", "MODERN", 0);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}