package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private String routeId = "route-123";
    private String ruleId;
    private String newTarget;
    private int version;
    private Instant effectiveDate;
    
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        this.aggregate = new LegacyTransactionRoute(routeId);
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        this.ruleId = "rule-abc";
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        this.newTarget = "MODERN";
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        this.effectiveDate = Instant.now();
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void theUpdateRoutingRuleCmdCommandIsExecuted() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            routeId,
            ruleId,
            newTarget,
            1, // Default valid version for success case unless overridden
            effectiveDate
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void aRoutingUpdatedEventIsEmitted() {
        assertThat(capturedException).isNull();
        assertThat(resultEvents).hasSize(1);
        assertThat(resultEvents.get(0).type()).isEqualTo("RoutingRuleUpdated");
    }

    // Failure Scenarios

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        this.aggregate = new LegacyTransactionRoute(routeId);
        this.aggregate.markDualProcessingViolation();
        // Setup valid command data for the attempt
        this.ruleId = "rule-abc";
        this.newTarget = "MODERN";
        this.effectiveDate = Instant.now();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        this.aggregate = new LegacyTransactionRoute(routeId);
        this.aggregate.markVersioningViolation();
        // Setup valid command data for the attempt
        this.ruleId = "rule-abc";
        this.newTarget = "MODERN";
        this.effectiveDate = Instant.now();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertThat(capturedException).isNotNull();
        // We expect an IllegalStateException or IllegalArgumentException depending on the specific invariant check
        assertThat(capturedException).isInstanceOf(IllegalStateException.class);
    }
}
