package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingRuleUpdatedEvent;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
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
    private UpdateRoutingRuleCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Handled in the command construction below, just ensuring state is clean
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Handled in command construction
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Handled in command construction
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // Construct a valid command
            this.command = new UpdateRoutingRuleCmd(
                    "route-123",
                    "rule-abc",
                    "VForce360", // Modern target
                    2,           // Valid version
                    Instant.now()
            );
            this.resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof RoutingRuleUpdatedEvent);

        RoutingRuleUpdatedEvent event = (RoutingRuleUpdatedEvent) resultingEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("route-123", event.aggregateId());
        assertEquals("VForce360", event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThat violatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-bad");
        // Use the test utility to force the invariant violation state
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-bad");
        // Use the test utility to force the invariant violation state
        aggregate.markVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNull(resultingEvents);
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
