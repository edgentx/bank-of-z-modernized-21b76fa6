package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;

public class S11Steps {

    private Transaction transaction;
    private WithdrawalPostedEvent lastEvent;
    private Exception thrownException;

    // State for building the command
    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        this.transaction = new Transaction(TransactionId.generate(), TransactionStatus.PENDING);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        this.accountNumber = "ACC-123-456";
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        this.amount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        this.currency = Currency.getInstance("USD");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        try {
            PostWithdrawalCmd cmd = new PostWithdrawalCmd(accountNumber, amount, currency);
            DomainEvent event = transaction.execute(cmd);
            if (event instanceof WithdrawalPostedEvent) {
                this.lastEvent = (WithdrawalPostedEvent) event;
            }
        } catch (DomainException e) {
            this.thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        Assertions.assertNotNull(lastEvent, "Expected WithdrawalPostedEvent to be emitted");
        Assertions.assertEquals(accountNumber, lastEvent.accountNumber());
        Assertions.assertEquals(amount, lastEvent.amount());
    }

    // --- Negative Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountGreaterThanZero() {
        this.transaction = new Transaction(TransactionId.generate(), TransactionStatus.PENDING);
        this.amount = BigDecimal.ZERO; // Invalid state
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesPostedImmutable() {
        this.transaction = new Transaction(TransactionId.generate(), TransactionStatus.POSTED); // Already posted
        this.amount = new BigDecimal("50.00");
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidBalance() {
        // Simulating a scenario where the aggregate logic would fail balance check
        // For this step, we setup the command with an invalid configuration contextually
        this.transaction = new Transaction(TransactionId.generate(), TransactionStatus.PENDING);
        this.amount = new BigDecimal("-100.00"); // Logic check fails
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected DomainException to be thrown");
        Assertions.assertTrue(thrownException instanceof DomainException);
    }
}