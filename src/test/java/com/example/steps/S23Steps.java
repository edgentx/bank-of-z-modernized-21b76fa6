package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S23Steps {

    private LegacyTransactionRoute route;
    private String transactionType;
    private String payload;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        route = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        this.transactionType = "WIRE_TRANSFER";
    }

    @Given("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        this.payload = "{\"amount\": 100, \"currency\": \"USD\"}";
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        // Default valid command setup for the happy path
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd(
            "tx-456", 
            this.transactionType, 
            this.payload, 
            "LEGACY", // Target System
            1 // Version
        );
        
        try {
            resultEvents = route.execute(cmd);
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
        assertEquals("tx-456", event.transactionId());
        assertEquals("LEGACY", event.targetSystem());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_routing_rules() {
        route = new LegacyTransactionRoute("route-bad");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        route = new LegacyTransactionRoute("-route-version");
    }

    // We overload the When step slightly to handle different command payloads derived from the violation context.
    // In a real framework, we might use a scenario context object.
    
    @When("the EvaluateRoutingCmd command is executed with invalid target system")
    public void the_evaluate_routing_cmd_command_is_executed_with_invalid_target() {
        // Violating "exactly one backend system" logic (e.g. null or ambiguous)
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("tx-bad", "TRANSFER", "{}", null, 1); 
        try {
            route.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @When("the EvaluateRoutingCmd command is executed with invalid version")
    public void the_evaluate_routing_cmd_command_is_executed_with_invalid_version() {
        // Violating "rules must be versioned" logic (e.g. version 0)
        EvaluateRoutingCmd cmd = new EvaluateRoutingCmd("tx-ver", "TRANSFER", "{}", "MODERN", 0);
        try {
            route.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
