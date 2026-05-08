package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingRuleUpdatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private UpdateRoutingRuleCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system")
    public void a_legacy_transaction_route_aggregate_with_dual_processing_violation() {
        aggregate = new LegacyTransactionRoute("route-123");
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback")
    public void a_legacy_transaction_route_aggregate_with_versioning_violation() {
        aggregate = new LegacyTransactionRoute("route-123");
        aggregate.markVersioningViolation();
    }

    @And("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Context setup - passed to command in 'When'
    }

    @And("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Context setup - passed to command in 'When'
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Context setup - passed to command in 'When'
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        // Create a valid command with defaults for positive flow, 
        // specific values don't matter much for Invariant checks unless specified
        command = new UpdateRoutingRuleCmd(
            "route-123", 
            "rule-A", 
            "MODERN", 
            Instant.now(), 
            2
        );

        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof RoutingRuleUpdatedEvent);
        
        RoutingRuleUpdatedEvent routingEvent = (RoutingRuleUpdatedEvent) event;
        assertEquals("routing.updated", routingEvent.type());
        assertEquals("route-123", routingEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on invariant, it could be IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
