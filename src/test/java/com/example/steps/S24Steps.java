package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingRuleUpdatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
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
    
    // Test Data
    private String testRouteId = "route-test-123";
    private String testRuleId = "rule-legacy-to-modern";
    private String testNewTarget = "MODERN";
    private int testNewVersion = 2;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        this.aggregate = new LegacyTransactionRoute(testRouteId);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system \(modern or legacy\) to prevent dual-processing\.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        this.aggregate = new LegacyTransactionRoute(testRouteId);
        // Marking as violating the specific invariant for this test case
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback\.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        this.aggregate = new LegacyTransactionRoute(testRouteId);
        // Marking as violating the specific invariant for this test case
        this.aggregate.markVersioningViolation();
    }

    @And("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Implicitly handled by test data setup
    }

    @And("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Implicitly handled by test data setup
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Implicitly handled by test data setup
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                testRouteId, 
                testRuleId, 
                testNewTarget, 
                testNewVersion, 
                Instant.now()
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof RoutingRuleUpdatedEvent, "Event should be RoutingRuleUpdatedEvent");
        
        RoutingRuleUpdatedEvent specificEvent = (RoutingRuleUpdatedEvent) event;
        assertEquals("RoutingRuleUpdated", specificEvent.type());
        assertEquals(testRouteId, specificEvent.aggregateId());
        assertEquals(testNewTarget, specificEvent.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }
}