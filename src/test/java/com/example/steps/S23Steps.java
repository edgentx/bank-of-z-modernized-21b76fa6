package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
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
        aggregate.configure(true, 1); // Modern enabled, version 1
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // Stored in context for the When step
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Stored in context for the When step
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-bad-dual");
        aggregate.configure(true, 1);
        // We use the payload to trigger the violation logic in the aggregate for this test scenario
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-bad-version");
        aggregate.configure(true, 0); // Version 0 is invalid based on our domain logic
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        try {
            // Determine payload based on aggregate state to trigger specific violations if needed
            Map<String, Object> payload;
            if (aggregate.getCurrentRuleVersion() == 0) {
                payload = Map.of("test", "data");
            } else if ("route-bad-dual".equals(aggregate.id())) {
                payload = Map.of("violation", "dual-write");
            } else {
                payload = Map.of("account", "12345");
            }

            command = new EvaluateRoutingCmd(aggregate.id(), "TRANSFER", payload);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        assertEquals("routing.evaluated", event.type());
        assertNotNull(event.targetSystem());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        // Verify the message contains parts of the invariant requirements
        assertTrue(thrownException.getMessage().contains("transaction must route to exactly one backend system") || 
                   thrownException.getMessage().contains("Routing rules must be versioned"));
    }
}