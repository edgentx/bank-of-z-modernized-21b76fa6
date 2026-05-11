package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.shared.Command;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Context setup managed in the When step via Command object construction
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context setup managed in the When step
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context setup managed in the When step
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "route-123",
                "rule-abc",
                "MODERN",
                Instant.now().plusSeconds(60),
                2
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("route-123", event.aggregateId());
        Assertions.assertEquals("MODERN", event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-violation-dual");
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute("route-violation-version");
        this.aggregate.markVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // In Java domain, domain errors are often Exceptions (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
