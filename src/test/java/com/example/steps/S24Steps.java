package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RuleUpdatedEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    // Scenario 1: Success
    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        // Create a valid aggregate instance (default state is valid)
        aggregate = new LegacyTransactionRoute("route-123");
        // Ensure state allows update (not in violation state)
        aggregate.setRulesVersion(1);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Command is constructed in 'When' clause, data assumed valid
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Command is constructed in 'When' clause, data assumed valid
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Command is constructed in 'When' clause, data assumed valid
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "route-123",
                "rule-42",
                "VForce360",
                Instant.now().plusSeconds(3600)
            );
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof RuleUpdatedEvent);
        
        RuleUpdatedEvent event = (RuleUpdatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("route-123", event.aggregateId());
        Assertions.assertEquals("VForce360", event.targetSystem());
    }

    // Scenario 2: Invariant violation (Dual processing)
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-dual");
        // Set state to "BAD": Already routed to one system, implying update would route to another
        // or simply flagged as violating the specific invariant.
        aggregate.markDualProcessingViolation();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected exception was not thrown");
        // Ideally we catch specific Domain Exception types, here we check message or type
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
        Assertions.assertTrue(caughtException.getMessage().contains("exactly one backend system"));
    }

    // Scenario 3: Invariant violation (Versioning)
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-ver");
        // Set state to "BAD": Rules version is invalid
        aggregate.markVersioningViolation();
    }

}
