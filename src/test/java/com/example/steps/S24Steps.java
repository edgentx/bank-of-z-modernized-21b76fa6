package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.shared.DomainEvent;
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
    private String routeId = "test-route-1";
    private String ruleId = "rule-101";
    private String newTarget;
    private Instant effectiveDate;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute(routeId);
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        this.ruleId = "rule-123";
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        this.newTarget = "MODERN";
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        this.effectiveDate = Instant.now().plusSeconds(3600);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(routeId, ruleId, newTarget, effectiveDate);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents, "Events should not be null");
        Assertions.assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        
        DomainEvent event = resultingEvents.get(0);
        Assertions.assertTrue(event instanceof RoutingUpdatedEvent, "Event should be instance of RoutingUpdatedEvent");
        
        RoutingUpdatedEvent routingEvent = (RoutingUpdatedEvent) event;
        Assertions.assertEquals("RoutingUpdated", routingEvent.type());
        Assertions.assertEquals(routeId, routingEvent.aggregateId());
        Assertions.assertEquals(newTarget, routingEvent.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.markDualProcessingViolation();
        // Setup defaults for command fields so the command itself is valid, but aggregate rejects it
        this.ruleId = "rule-123";
        this.newTarget = "MODERN";
        this.effectiveDate = Instant.now();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute(routeId);
        aggregate.markVersioningViolation();
        // Setup defaults
        this.ruleId = "rule-123";
        this.newTarget = "MODERN";
        this.effectiveDate = Instant.now();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(
                capturedException instanceof IllegalStateException,
                "Expected IllegalStateException, got " + capturedException.getClass().getSimpleName()
        );
    }
}
