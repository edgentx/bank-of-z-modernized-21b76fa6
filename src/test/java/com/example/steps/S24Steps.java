package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.model.RoutingUpdatedEvent;
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
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Scenario context handled in When step
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Scenario context handled in When step
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Scenario context handled in When step
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd("route-123", "rule-1", "VForce360", 1, Instant.now());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("route-123", event.aggregateId());
        assertEquals("VForce360", event.newTarget());
        assertEquals("RoutingUpdated", event.type());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-bad-1");
        aggregate.setViolationDualProcessing(true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("dual-processing") || thrownException.getMessage().contains("rollback"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-bad-2");
        aggregate.setViolationVersioning(true);
    }
}
