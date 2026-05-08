package com.example.steps;

import com.example.domain.routing.model.LegacyTransactionRoute;
import com.example.domain.routing.model.RoutingRuleUpdatedEvent;
import com.example.domain.routing.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private UpdateRoutingRuleCmd cmd;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Rule ID valid by default in the context of the command construction below
    }

    @And("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Target valid by default in the context of the command construction below
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Date valid by default
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        // Default valid command setup
        if (cmd == null) {
            cmd = new UpdateRoutingRuleCmd("route-123", "rule-1", "MODERN", Instant.now());
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingRuleUpdatedEvent);
        
        RoutingRuleUpdatedEvent event = (RoutingRuleUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("route-123", event.aggregateId());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        // Setup command that violates the rule (e.g. targeting 'BOTH')
        cmd = new UpdateRoutingRuleCmd("route-123", "rule-dual", "BOTH", Instant.now());
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        // Ensure no events were emitted
        assertTrue(aggregate.uncommittedEvents().isEmpty());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        // Setup command that violates versioning (e.g. bad rule ID)
        cmd = new UpdateRoutingRuleCmd("route-123", "rule-noversion", "MODERN", Instant.now());
        aggregate = new LegacyTransactionRoute("route-123");
    }
}
