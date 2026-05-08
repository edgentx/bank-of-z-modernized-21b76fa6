package com.example.steps;

import com.example.domain.routing.model.LegacyTransactionRouteAggregate;
import com.example.domain.routing.model.RoutingUpdatedEvent;
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

    private LegacyTransactionRouteAggregate aggregate;
    private String routeId = "route-1";
    private String ruleId;
    private String newTarget;
    private Instant effectiveDate;
    private int version;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRouteAggregate(routeId);
    }

    @And("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        this.ruleId = "rule-123";
    }

    @And("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        this.newTarget = "MODERN";
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        this.effectiveDate = Instant.now();
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, effectiveDate, 1);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals(routeId, event.aggregateId());
        assertEquals(ruleId, event.ruleId());
        assertEquals(newTarget, event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_exclusive_routing() {
        aggregate = new LegacyTransactionRouteAggregate(routeId);
        // Simulate state where it's already modern
        aggregate.setSystemFlags(false, true); 
        this.ruleId = "rule-123";
        this.newTarget = "MODERN"; // Trying to route to MODERN again (or dual processing simulation)
        this.effectiveDate = Instant.now();
        this.version = 1;
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect an IllegalStateException based on the aggregate implementation
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRouteAggregate(routeId);
        // Add an existing rule with version 1
        aggregate.addExistingRule("rule-123", "LEGACY", Instant.now().minusSeconds(3600), 1);
        
        this.ruleId = "rule-123";
        this.newTarget = "MODERN";
        this.effectiveDate = Instant.now();
        this.version = 0; // Version is less than existing (1)
    }

}
