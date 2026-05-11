package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1 & 2 & 3 Setup
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        // Ensure clean state
        this.aggregate.setEvaluated(false);
    }

    // Scenario 1 Specifics
    @And("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // Context for the command construction in 'When'
    }

    @And("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // Context
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // Context
    }

    // Scenario 1 Execution
    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "route-123",
                "rule-abc",
                "MODERN",
                1,
                Instant.now()
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // Scenario 1 Validation
    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("MODERN", event.newTarget());
        Assertions.assertEquals("route-123", event.aggregateId());
    }

    // Scenario 2: Dual Processing Violation
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        this.aggregate = new LegacyTransactionRoute("route-bad-dual");
        this.aggregate.markDualProcessingViolation();
    }

    // Scenario 3: Versioning Violation
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        this.aggregate = new LegacyTransactionRoute("route-bad-ver");
        this.aggregate.markVersioningViolation();
    }

    // Scenario 2 & 3 Validation
    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected a domain error exception, but none was thrown");
        // In our domain logic, violations throw IllegalStateException
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}
