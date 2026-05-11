package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingUpdatedEvent;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private Exception caughtException;
    private RoutingUpdatedEvent resultEvent;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        this.caughtException = null;
        this.resultEvent = null;
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // This step simply establishes the context, data is created in the When step
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateRoutingRuleCmd(
                "route-123",
                "rule-alpha",
                "VForce360",
                Instant.now().plusSeconds(3600)
            );
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                this.resultEvent = (RoutingUpdatedEvent) events.get(0);
            }
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvent, "Expected an event to be emitted");
        assertEquals("RoutingUpdated", resultEvent.type());
        assertEquals("route-123", resultEvent.aggregateId());
        assertEquals("rule-alpha", resultEvent.ruleId());
        assertEquals("VForce360", resultEvent.newTarget());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        aggregate.markDualProcessingViolation();
        this.caughtException = null;
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-123");
        aggregate.markVersioningViolation();
        this.caughtException = null;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain error exception, but none was thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException, got " + caughtException.getClass().getSimpleName());
    }
}
