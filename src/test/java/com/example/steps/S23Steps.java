package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.Assert.*;

public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private EvaluateRoutingCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute EvaluateRoutingCmd
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
    }

    @And("a valid transactionType is provided")
    public void a_valid_transaction_type_is_provided() {
        // Transaction type will be passed in the command
    }

    @And("a valid payload is provided")
    public void a_valid_payload_is_provided() {
        // Payload will be passed in the command
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed() {
        try {
            cmd = new EvaluateRoutingCmd("route-1", "TX_TYPE", Map.of("key", "value"), "MODERN", 1);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void a_routing_evaluated_event_is_emitted() {
        assertNotNull("Result events should not be null", resultEvents);
        assertFalse("Result events should not be empty", resultEvents.isEmpty());
        assertTrue("First event should be RoutingEvaluatedEvent", resultEvents.get(0) instanceof RoutingEvaluatedEvent);
        
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultEvents.get(0);
        assertEquals("routing.evaluated", event.type());
        assertEquals("MODERN", event.targetSystem());
    }

    // Scenario: EvaluateRoutingCmd rejected — Dual processing
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-2");
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed_dual_violation() {
        try {
            // "BOTH" violates the single system rule
            cmd = new EvaluateRoutingCmd("route-2", "TX_TYPE", Map.of(), "BOTH", 1);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", capturedException);
        assertTrue("Expected IllegalArgumentException", capturedException instanceof IllegalArgumentException);
        assertTrue("Exception message should mention dual-processing", capturedException.getMessage().contains("dual-processing"));
    }

    // Scenario: EvaluateRoutingCmd rejected — Versioning
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-3");
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void the_evaluate_routing_cmd_command_is_executed_version_violation() {
        try {
            // version 0 or negative violates versioning rule
            cmd = new EvaluateRoutingCmd("route-3", "TX_TYPE", Map.of(), "MODERN", 0);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_version() {
        assertNotNull("Expected an exception to be thrown", capturedException);
        assertTrue("Expected IllegalArgumentException", capturedException instanceof IllegalArgumentException);
        assertTrue("Exception message should mention versioned", capturedException.getMessage().contains("versioned"));
    }
}
