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
    private List<DomainEvent> resultingEvents;

    // Given steps

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Parameter stored in context for execution step, or we can define defaults here
        // For this exercise, we'll assume the When step constructs the command with valid defaults,
        // or we store state in member variables if dynamic.
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // See above
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // See above
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-dual-bad");
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-version-bad");
        this.aggregate.markVersioningViolation();
    }

    // When steps

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                    aggregate.id(),
                    "rule-1",
                    "MODERN",
                    2,
                    Instant.now()
            );
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // Then steps

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof RoutingUpdatedEvent);

        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultingEvents.get(0);
        assertEquals("MODERN", event.newTarget());
        assertEquals(2, event.newVersion());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect IllegalStateException or IllegalArgumentException based on invariants
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}