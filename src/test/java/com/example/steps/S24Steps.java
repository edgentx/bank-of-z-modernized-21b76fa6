package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private UpdateRoutingRuleCmd cmd;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        this.aggregate.setForceDualProcessingViolation(false);
        this.aggregate.setForceVersioningViolation(false);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        this.aggregate.setForceDualProcessingViolation(true);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        this.aggregate.setForceVersioningViolation(true);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Rule ID is part of command construction, we store it or just note it
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Target is part of command construction
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Date is part of command construction
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // We construct the command here. In a real test, these might come from scenario context, but for this simple feature
            // we can assume valid defaults for the happy path, and the violations are handled by Aggregate state flags.
            this.cmd = new UpdateRoutingRuleCmd(
                    "route-123",
                    "rule-abc",
                    "VForce360", // Modern target
                    Instant.now(),
                    2 // New version
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("route-123", event.aggregateId());
        Assertions.assertEquals("VForce360", event.newTarget());
        Assertions.assertEquals(2, event.effectiveVersion());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // In this architecture, invariant violations throw RuntimeExceptions (IllegalStateException/IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
