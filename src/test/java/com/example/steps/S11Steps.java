package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S11Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private WithdrawalPosted resultingEvent;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Create a transaction that is ready to be posted (PENDING)
        this.transaction = new Transaction(
            UUID.randomUUID(),
            "123456789",
            new BigDecimal("1000.00"),
            "USD",
            TransactionStatus.PENDING
        );
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount() {
        this.transaction = new Transaction(
            UUID.randomUUID(),
            "123456789",
            new BigDecimal("1000.00"),
            "USD",
            TransactionStatus.PENDING
        );
        this.amount = new BigDecimal("-50.00");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_posted() {
        // Create an already posted transaction
        this.transaction = new Transaction(
            UUID.randomUUID(),
            "123456789",
            new BigDecimal("1000.00"),
            "USD",
            TransactionStatus.POSTED
        );
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance() {
        // Low balance
        this.transaction = new Transaction(
            UUID.randomUUID(),
            "123456789",
            new BigDecimal("10.00"),
            "USD",
            TransactionStatus.PENDING
        );
        this.amount = new BigDecimal("500.00");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "123456789";
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.amount == null) {
            this.amount = new BigDecimal("50.00");
        }
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        PostWithdrawalCmd cmd = new PostWithdrawalCmd(transaction.getId(), accountNumber, amount, currency);
        try {
            this.resultingEvent = transaction.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent);
        Assertions.assertNotNull(resultingEvent.getTransactionId());
        Assertions.assertEquals(amount, resultingEvent.getAmount());
        Assertions.assertEquals(accountNumber, resultingEvent.getAccountNumber());
        Assertions.assertNull(thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertNull(resultingEvent);
    }
}
