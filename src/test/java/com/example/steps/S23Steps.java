package com.example.steps;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-23: LegacyTransactionRoute.
 */
public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private String transactionType;
    private Map<String, Object> payload;
    private Integer targetRulesVersion;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        this.transactionType = "TRANSFER";
    }

    @Given("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        this.payload = Map.of("source", "web", "destination", "external");
    }

    // Scenario 1: Success
    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        try {
            // Default version for success scenario
            int version = (targetRulesVersion != null) ? targetRulesVersion : 1;
            EvaluateRoutingCmd cmd = new EvaluateRoutingCmd(aggregate.id(), transactionType, payload, version);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof RoutingEvaluatedEvent, "Event should be RoutingEvaluatedEvent");
    }

    // Scenario 2: Dual-processing violation
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-violation-dual");
        this.transactionType = "DUAL_TX";
        // Simulate violation: payload contains flag suggesting dual routing
        this.payload = Map.of("dual-route", true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception should be IllegalArgumentException");
        assertTrue(caughtException.getMessage().contains("exactly one backend system"), "Exception message should match constraint");
    }

    // Scenario 3: Versioning violation
    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-violation-version");
        this.transactionType = "VERSION_TX";
        this.payload = Map.of();
        // Simulate violation: version is 0 or negative
        this.targetRulesVersion = 0;
    }
}
