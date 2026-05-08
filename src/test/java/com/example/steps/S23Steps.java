package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private EvaluateRoutingCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // We'll construct the command in the 'When' step, but we store state here if needed.
        // For this BDD style, we often define the command parts in variables.
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Same as above
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        // Constructing a valid command for the success scenario
        command = new EvaluateRoutingCmd(
                "route-123",
                "TRANSFER",
                Map.of("amount", 100),
                "MODERN", // Target System
                1          // Rule Version
        );
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof RoutingEvaluatedEvent, "Event should be RoutingEvaluatedEvent");

        RoutingEvaluatedEvent routingEvent = (RoutingEvaluatedEvent) event;
        assertEquals("routing.evaluated", routingEvent.type());
        assertEquals("route-123", routingEvent.aggregateId());
        assertEquals("MODERN", routingEvent.targetSystem());
    }

    // --- Error Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-bad-dual");
        aggregate.markAsDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-bad-version");
        aggregate.markAsVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the error message matches the invariant description
        assertTrue(
                thrownException.getMessage().contains("dual-processing") || 
                thrownException.getMessage().contains("versioned"),
                "Error message should match the invariant violation"
        );
    }
}
