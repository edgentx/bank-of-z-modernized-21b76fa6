package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class S24Steps {

    // State
    private LegacyTransactionRoute aggregate;
    private Exception capturedException;
    
    // Dependencies
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();

    // Setup Givens
    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Rule ID is part of the command construction, handled in When
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Target is part of the command construction, handled in When
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Effective Date is part of the command construction, handled in When
    }

    // Violation Givens
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute("route-bad-dual");
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRouteAggregate("route-bad-version");
        this.aggregate.markVersioningViolation();
    }

    // Action
    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        // Construct a valid command by default. 
        // Invariants are checked inside the aggregate based on state or command params.
        // For S-24, the command payload is simple.
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            aggregate.id(), 
            "rule-456", 
            "MODERN", 
            Instant.now().plusSeconds(3600)
        );

        try {
            aggregate.execute(cmd);
            // Persist if successful (standard pattern)
            repository.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    // Outcomes
    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        
        Object event = aggregate.uncommittedEvents().get(0);
        Assertions.assertTrue(event instanceof RoutingUpdatedEvent, "Event should be RoutingUpdatedEvent");
        
        RoutingUpdatedEvent updated = (RoutingUpdatedEvent) event;
        Assertions.assertEquals("rule-456", updated.ruleId());
        Assertions.assertEquals("MODERN", updated.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Specific error messages can be asserted here if desired, but class type is usually sufficient for BDD
        if (aggregate instanceof LegacyTransactionRoute agg && agg.isDualProcessingViolation()) {
            Assertions.assertTrue(capturedException instanceof IllegalStateException);
            Assertions.assertTrue(capturedException.getMessage().contains("dual-processing"));
        } else {
            Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
            Assertions.assertTrue(capturedException.getMessage().contains("versioned") || capturedException.getMessage().contains("rollback"));
        }
    }
}
