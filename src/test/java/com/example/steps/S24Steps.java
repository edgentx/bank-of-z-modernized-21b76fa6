package com.example.steps;

import com.example.domain.legacytransactionroute.model.LegacyTransactionRouteAggregate;
import com.example.domain.legacytransactionroute.model.UpdateRoutingRuleCmd;
import com.example.domain.legacytransactionroute.model.RoutingRuleUpdatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRouteAggregate aggregate;
    private UpdateRoutingRuleCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRouteAggregate("route-123");
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Rule ID handled in command construction below
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Target handled in command construction below
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Date handled in command construction below
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            // Assume defaults are valid
            if (command == null) {
                command = new UpdateRoutingRuleCmd("route-123", "rule-1", "MODERN", Instant.now(), 1);
            }
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof RoutingRuleUpdatedEvent);

        RoutingRuleUpdatedEvent event = (RoutingRuleUpdatedEvent) resultingEvents.get(0);
        assertEquals("route-123", event.aggregateId());
        assertEquals("RoutingRuleUpdated", event.type());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_single_target() {
        aggregate = new LegacyTransactionRouteAggregate("route-123");
        aggregate.setViolateSingleTarget(true);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRouteAggregate("route-123");
        aggregate.setViolateVersioning(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
