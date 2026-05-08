package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.EvaluateRoutingCmd;
import com.example.domain.transaction.model.LegacyTransactionRoute;
import com.example.domain.transaction.model.RoutingEvaluatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private String transactionType;
    private Map<String, Object> payload;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        this.transactionType = "TRANSFER";
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        this.payload = Map.of("amount", 100);
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        try {
            Command cmd = new EvaluateRoutingCmd("route-123", transactionType, payload);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        assertEquals("routing.evaluated", event.type());
        assertNotNull(event.targetSystem()); // "MODERN" or "LEGACY"
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_uniqueness() {
        aggregate = new LegacyTransactionRoute("route-123");
        // Simulate that it is already routed
        aggregate.setAlreadyRouted("MODERN");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Checking for the specific invariant violation message is good practice
        assertTrue(capturedException.getMessage().contains("Dual-processing") || 
                   capturedException.getMessage().contains("already been routed"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-123");
        // Simulate an invalid version state
        aggregate.setInvalidRulesVersion(0); // 0 or negative is invalid
    }
}