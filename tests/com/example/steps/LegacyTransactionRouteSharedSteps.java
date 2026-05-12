package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by every LegacyTransactionRoute-aggregate
 * story (S-23 EvaluateRouting, S-24 UpdateRoutingRule). Each Given
 * seeds {@link LegacyTransactionRouteSharedContext#aggregate}.
 */
public class LegacyTransactionRouteSharedSteps {

    private final LegacyTransactionRouteSharedContext ctx;

    public LegacyTransactionRouteSharedSteps(LegacyTransactionRouteSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid LegacyTransactionRoute aggregate")
    public void aValidLegacyTransactionRouteAggregate() {
        ctx.aggregate = new LegacyTransactionRoute("ROUTE-1");
        ctx.repository.save(ctx.aggregate);
    }

    @And("a valid transactionType is provided")
    public void aValidTransactionTypeIsProvided() {
        // Command construction is deferred to the @When step.
    }

    @And("a valid payload is provided")
    public void aValidPayloadIsProvided() {
        // Command construction is deferred to the @When step.
    }

    @And("a valid ruleId is provided")
    public void aValidRuleIdIsProvided() {
        // Command construction is deferred to the @When step.
    }

    @And("a valid newTarget is provided")
    public void aValidNewTargetIsProvided() {
        // Command construction is deferred to the @When step.
    }

    @And("a valid effectiveDate is provided")
    public void aValidEffectiveDateIsProvided() {
        // Command construction is deferred to the @When step.
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system \\(modern or legacy) to prevent dual-processing.")
    public void aLegacyTransactionRouteAggregateThatViolatesDualProcessing() {
        ctx.aggregate = new LegacyTransactionRoute("ROUTE-DUAL-ERR");
        ctx.aggregate.markDualProcessingViolation();
        ctx.repository.save(ctx.aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void aLegacyTransactionRouteAggregateThatViolatesVersioning() {
        ctx.aggregate = new LegacyTransactionRoute("ROUTE-VER-ERR");
        ctx.aggregate.markVersioningViolation();
        ctx.repository.save(ctx.aggregate);
    }
}
