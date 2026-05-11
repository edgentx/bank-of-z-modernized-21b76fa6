package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingRuleUpdatedEvent;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private UpdateRoutingRuleCmd command;
    private Exception capturedException;
    private String providedRuleId;
    private String providedNewTarget;
    private LocalDate providedEffectiveDate;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        UUID id = UUID.randomUUID();
        this.aggregate = new LegacyTransactionRoute(id);
    }

    @Given("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        this.providedRuleId = "RULE-101";
    }

    @Given("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        this.providedNewTarget = "MODERN";
    }

    @Given("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        this.providedEffectiveDate = LocalDate.now().plusDays(1);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        try {
            // We assume a default valid aggregate context if not specified otherwise
            if (this.aggregate == null) {
                 this.aggregate = new LegacyTransactionRoute(UUID.randomUUID());
            }
            // Initialize rule for versioning checks if needed for specific scenario context
            // But for "valid" scenario, we assume clean slate or valid state.
            
            this.command = new UpdateRoutingRuleCmd(
                this.aggregate.getId(),
                this.providedRuleId,
                this.providedNewTarget,
                this.providedEffectiveDate
            );
            
            this.aggregate.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        List<com.example.domain.shared.DomainEvent> events = this.aggregate.getUncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have uncommitted events");
        Assertions.assertTrue(events.get(0) instanceof RoutingRuleUpdatedEvent, "Event should be RoutingRuleUpdatedEvent");
    }

    // --- Failure Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesBackendUniqueness() {
        this.aggregate = new LegacyTransactionRoute(UUID.randomUUID());
        // The violation is triggered by the command data, not necessarily the aggregate state,
        // based on the invariant "A transaction must route to exactly one backend system... to prevent dual processing"
        // Let's assume an invalid target or configuration triggers this.
        // The prompt implies the command or state leads to this. Let's set a target that implies dual or invalid.
        this.providedNewTarget = "DUAL_PROCESSING_MODE"; // Simulating violation
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute(UUID.randomUUID());
        this.aggregate.addRuleForTest("RULE-101", "LEGACY", 5); 
        // The aggregate thinks version is 5.
        // If we execute a command that doesn't respect versioning (e.g., not providing expected version),
        // or if the internal logic checks strictly that versions must increment sequentially,
        // we can trigger the error. 
        // Based on implementation: If existing version >= aggregate version, error.
        // We'll rely on the Logic inside the aggregate to throw.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Typically a domain error might be a specific exception type, 
        // but IllegalStateException is a valid Java domain exception.
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
