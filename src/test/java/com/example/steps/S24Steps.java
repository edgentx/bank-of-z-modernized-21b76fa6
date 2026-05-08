package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Test Data
    private static final String TEST_ROUTE_ID = "route-123";
    private static final String TEST_RULE_ID = "rule-abc";
    private static final String TEST_TARGET = "VForce360";
    private static final Instant TEST_DATE = Instant.now();

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute(TEST_ROUTE_ID);
        this.caughtException = null;
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute(TEST_ROUTE_ID);
        // Mark the aggregate to simulate the invariant violation state
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute(TEST_ROUTE_ID);
        // Mark the aggregate to simulate the invariant violation state
        aggregate.markVersioningViolation();
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Context setup, data handled in 'When' step
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context setup, data handled in 'When' step
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context setup, data handled in 'When' step
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(TEST_ROUTE_ID, TEST_RULE_ID, TEST_TARGET, TEST_DATE);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertInstanceOf(RoutingUpdatedEvent.class, event, "Event must be of type RoutingUpdatedEvent");
        
        RoutingUpdatedEvent routingEvent = (RoutingUpdatedEvent) event;
        assertEquals("routing.updated", routingEvent.type());
        assertEquals(TEST_ROUTE_ID, routingEvent.aggregateId());
        assertEquals(TEST_RULE_ID, routingEvent.ruleId());
        assertEquals(TEST_TARGET, routingEvent.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // The specific message or type can be asserted here based on the violation
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
