package com.example.steps;

import com.example.domain.routing.model.LegacyTransactionRoute;
import com.example.domain.routing.model.UpdateRoutingRuleCmd;
import com.example.domain.routing.model.RoutingRuleUpdatedEvent;
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
    private UpdateRoutingRuleCmd cmd;

    // --- Given Steps ---

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Handled in the @When step construction for simplicity, or stored here if context implies
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Handled in the @When step
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Handled in the @When step
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-bad-1");
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-bad-2");
        aggregate.markVersioningViolation();
    }

    // --- When Steps ---

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        // Construct a valid command by default.
        // In a real flow, these might come from context, but for this feature
        // we construct them here to ensure validity unless the aggregate state causes failure.
        cmd = new UpdateRoutingRuleCmd(
                aggregate.id(),
                "rule-123",
                "MODERN",
                Instant.now(),
                2
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Then Steps ---

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof RoutingRuleUpdatedEvent, "Event must be RoutingRuleUpdatedEvent");

        RoutingRuleUpdatedEvent event = (RoutingRuleUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("MODERN", event.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        assertTrue(
                caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Exception should be a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
