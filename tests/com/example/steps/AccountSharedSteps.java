package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by all Account-aggregate stories (S-5, ...).
 * Each Given seeds {@link AccountSharedContext#aggregate}; story-specific
 * step classes ({@code S5Steps}) read from that context in their @When/@Then methods.
 * The "the command is rejected with a domain error" assertion lives in
 * {@link CommonSteps} and reads {@link ScenarioContext#thrownException}.
 */
public class AccountSharedSteps {

    private final AccountSharedContext ctx;

    public AccountSharedSteps(AccountSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        ctx.aggregate = new AccountAggregate("acct-1");
        ctx.repository.save(ctx.aggregate);
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // No-op: accountType is supplied when the command is constructed in @When.
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // No-op: initialDeposit is supplied when the command is constructed in @When.
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinBalance() {
        ctx.aggregate = new AccountAggregate("acct-min-balance");
        ctx.aggregate.markMinBalanceViolation();
        ctx.repository.save(ctx.aggregate);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        ctx.aggregate = new AccountAggregate("acct-inactive");
        ctx.aggregate.markActiveStatusViolation();
        ctx.repository.save(ctx.aggregate);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueAccountNumber() {
        ctx.aggregate = new AccountAggregate("acct-duplicate");
        ctx.aggregate.markUniqueAccountNumberViolation();
        ctx.repository.save(ctx.aggregate);
    }

}
