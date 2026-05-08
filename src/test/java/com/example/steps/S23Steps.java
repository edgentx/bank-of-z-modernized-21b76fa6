package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingEvaluatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

    private LegacyTransactionRoute aggregate;
    private EvaluateRoutingCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("test-route-1");
    }

    @Given("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        // Logic handled in When block construction for simplicity in this context
    }

    @Given("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        // Logic handled in When block construction
    }

    @When("the EvaluateRoutingCmd command is executed")
    public void theEvaluateRoutingCmdCommandIsExecuted() {
        try {
            cmd = new EvaluateRoutingCmd("test-route-1", "CREDIT_CARD", Map.of("amount", 100));
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.evaluated event is emitted")
    public void aRoutingEvaluatedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof RoutingEvaluatedEvent);
        RoutingEvaluatedEvent event = (RoutingEvaluatedEvent) resultingEvents.get(0);
        assertEquals("routing.evaluated", event.type());
        assertEquals("test-route-1", event.aggregateId());
    }

    // --- Scenario 2: Dual Processing Invariant ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("test-route-dual");
        // Using the helper method to simulate a state that violates the invariant
        // or triggering a condition where the check would fail.
        // In this design, we set the internal state to trigger the invariant check.
        aggregate.setDualProcessingViolation();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("exactly one backend system"));
    }

    // --- Scenario 3: Versioning Invariant ---

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("test-route-version");
        aggregate.setVersioningViolation();
    }
}
