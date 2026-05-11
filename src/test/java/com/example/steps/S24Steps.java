package com.example.steps;

import com.example.domain.legacybridge.model.*;
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
    private Throwable thrownException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
        thrownException = null;
        resultingEvents = null;
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Context setup, handled in When block
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Context setup, handled in When block
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Context setup, handled in When block
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateRoutingRuleCmd("route-123", "rule-1", "MODERN", Instant.now().plusSeconds(3600));
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof RoutingUpdatedEvent);

        var event = (RoutingUpdatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("route-123", event.aggregateId());
        Assertions.assertEquals("rule-1", event.ruleId());
        Assertions.assertEquals("MODERN", event.newTarget());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-123");
        aggregate.markDualProcessingViolation();
        thrownException = null;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Ideally check for specific domain exception type, but IllegalStateException works for invariant violation here
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertTrue(thrownException.getMessage().contains("dual-processing"));
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-123");
        aggregate.markVersioningViolation();
        thrownException = null;
    }
}
