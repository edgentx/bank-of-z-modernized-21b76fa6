package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class S11Steps {

    private Transaction transaction;
    private TransactionSnapshot snapshot;
    private PostWithdrawalCmd command;
    private WithdrawalPostedEvent resultingEvent;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        UUID accountId = UUID.randomUUID();
        // Assume sufficient balance
        this.snapshot = new TransactionSnapshot(accountId, new BigDecimal("1000.00"), false);
        this.transaction = new Transaction(snapshot);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Normally part of the aggregate context, but we can ensure the snapshot has it
        Assertions.assertNotNull(this.snapshot.accountId());
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Will be used in the When step
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Will be used in the When step
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount() {
        UUID accountId = UUID.randomUUID();
        this.snapshot = new TransactionSnapshot(accountId, new BigDecimal("1000.00"), false);
        this.transaction = new Transaction(snapshot);
        
        // The violation is in the command we will execute, not the aggregate state
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_altered() {
        UUID accountId = UUID.randomUUID();
        // Aggregate is already posted
        this.snapshot = new TransactionSnapshot(accountId, new BigDecimal("1000.00"), true);
        this.transaction = new Transaction(snapshot);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance() {
        UUID accountId = UUID.randomUUID();
        // Balance is zero or low
        this.snapshot = new TransactionSnapshot(accountId, BigDecimal.ZERO, false);
        this.transaction = new Transaction(snapshot);
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        try {
            if (transaction.getBalance().compareTo(new BigDecimal("1000.00")) == 0 && !transaction.isPosted()) {
                 // Standard case
                 this.command = new PostWithdrawalCmd(transaction.getAccountId(), new BigDecimal("50.00"), Currency.getInstance("USD"));
            } else if (transaction.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                 // Overdraft case
                 this.command = new PostWithdrawalCmd(transaction.getAccountId(), new BigDecimal("50.00"), Currency.getInstance("USD"));
            } else if (transaction.isPosted()) {
                 // Already posted case
                 this.command = new PostWithdrawalCmd(transaction.getAccountId(), new BigDecimal("50.00"), Currency.getInstance("USD"));
            } else {
                 // Fallback for the 'invalid amount' scenario
                 this.command = new PostWithdrawalCmd(transaction.getAccountId(), new BigDecimal("-10.00"), Currency.getInstance("USD"));
            }
            
            this.resultingEvent = transaction.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent);
        Assertions.assertNotNull(resultingEvent.transactionId());
        Assertions.assertEquals(command.accountId(), resultingEvent.accountId());
        Assertions.assertEquals(command.amount(), resultingEvent.amount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || 
                            thrownException instanceof IllegalStateException);
    }
}
