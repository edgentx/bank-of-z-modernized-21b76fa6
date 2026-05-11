package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private final LegacyTransactionRouteRepository repo = new InMemoryLegacyTransactionRouteRepository();
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Nop - handled in When
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Nop - handled in When
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Nop - handled in When
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        var cmd = new UpdateRoutingRuleCmd("route-123", "rule-1", "MODERN", 1, Instant.now());
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("routing.updated", resultingEvents.get(0).type());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        aggregate = new LegacyTransactionRoute("route-violation-1");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        aggregate = new LegacyTransactionRoute("route-violation-2");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
    }
}
