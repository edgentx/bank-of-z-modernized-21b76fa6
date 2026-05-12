package com.example.steps;

import com.example.domain.statement.model.StatementAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by all Statement-aggregate stories (S-8, ...).
 * Each Given seeds {@link StatementSharedContext#aggregate}; story-specific
 * step classes ({@code S8Steps}) read from that context in their @When/@Then methods.
 *
 * The "a valid accountNumber is provided" step is supplied by {@code S6Steps}
 * (Account aggregate) and reused here via picocontainer glue. The
 * "the command is rejected with a domain error" assertion lives in the
 * Account/Customer shared steps; Statement rejection scenarios drop into
 * the same global step text and inspect their own context via @When in
 * {@link S8Steps}.
 */
public class StatementSharedSteps {

    private final StatementSharedContext ctx;

    public StatementSharedSteps(StatementSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        ctx.aggregate = new StatementAggregate("stmt-1");
        ctx.repository.save(ctx.aggregate);
    }

    @And("a valid periodEnd is provided")
    public void aValidPeriodEndIsProvided() {
        // No-op: periodEnd is supplied when the command is constructed in @When.
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        ctx.aggregate = new StatementAggregate("stmt-open-period");
        ctx.aggregate.markClosedPeriodViolation();
        ctx.repository.save(ctx.aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        ctx.aggregate = new StatementAggregate("stmt-bad-opening");
        ctx.aggregate.markOpeningBalanceViolation();
        ctx.repository.save(ctx.aggregate);
    }
}
