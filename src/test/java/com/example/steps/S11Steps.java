package com.example.steps;

import com.example.domain.PostWithdrawalCmd;
import com.example.domain.S11Event;
import com.example.domain.Transaction;
import com.example.domain.TransactionState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;

public class S11Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;
    private Exception caughtException;
    private S11Event resultingEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.transaction = new Transaction();
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountNumber = "ACC-123-456";
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = Currency.getInstance("USD");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            PostWithdrawalCmd cmd = new PostWithdrawalCmd(accountNumber, amount, currency);
            resultingEvent = transaction.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent, "Event should not be null");
        Assertions.assertTrue(resultingEvent instanceof S11Event.WithdrawalPosted);
    }

    // --- Error Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_zero_amount() {
        this.transaction = new Transaction();
        this.amount = BigDecimal.ZERO;
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        // Create a transaction and post it to make it immutable
        this.transaction = new Transaction();
        transaction.execute(new PostWithdrawalCmd("ACC-123", new BigDecimal("10"), Currency.getInstance("USD")));
        // Now it is in POSTED state
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_logic() {
        this.transaction = new Transaction();
        this.accountNumber = "ACC-BAD-DEBT";
        // Withdraw a huge amount that would logically make the balance invalid
        this.amount = new BigDecimal("99999999.00");
        this.currency = Currency.getInstance("USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || 
                              caughtException instanceof IllegalArgumentException);
    }
}
