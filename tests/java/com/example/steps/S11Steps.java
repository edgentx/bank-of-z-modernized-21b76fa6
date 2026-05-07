package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private Transaction transaction;
    private Currency currency;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal currentBalance;
    private Exception caughtException;
    private TransactionEvent resultingEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
        this.currentBalance = new BigDecimal("1000.00");
        this.transaction = new Transaction(UUID.randomUUID(), accountNumber, currentBalance, currency, TransactionStatus.PENDING);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountNumber = "ACC-123";
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("50.00");
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = Currency.getInstance("USD");
    }

    // --- Scenarios for Violations ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_zero_amount() {
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
        this.currentBalance = new BigDecimal("1000.00");
        this.transaction = new Transaction(UUID.randomUUID(), accountNumber, currentBalance, currency, TransactionStatus.PENDING);
        // The violation will be in the command amount
        this.amount = BigDecimal.ZERO;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
        this.currentBalance = new BigDecimal("1000.00");
        // Create a transaction that is already POSTED
        this.transaction = new Transaction(UUID.randomUUID(), accountNumber, currentBalance, currency, TransactionStatus.POSTED);
        this.amount = new BigDecimal("50.00");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_overdraft() {
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
        // Current balance is 100
        this.currentBalance = new BigDecimal("100.00");
        this.transaction = new Transaction(UUID.randomUUID(), accountNumber, currentBalance, currency, TransactionStatus.PENDING);
        // Trying to withdraw 1000
        this.amount = new BigDecimal("1000.00");
    }

    // --- Action ---

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            // Using the 4-arg constructor as per feedback
            PostWithdrawalCmd cmd = new PostWithdrawalCmd(transaction.getId(), amount, currency, accountNumber);
            this.resultingEvent = transaction.execute(cmd);
        } catch (DomainException | IllegalArgumentException e) {
            this.caughtException = e;
        } catch (Exception e) {
            this.caughtException = new RuntimeException(e);
        }
    }

    // --- Outcomes ---

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultingEvent, "Event should not be null");
        assertTrue(resultingEvent instanceof WithdrawalPostedEvent, "Event should be WithdrawalPostedEvent");
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultingEvent;
        assertEquals(amount, event.getAmount());
        assertEquals(accountNumber, event.getAccountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for our specific domain exception or IllegalArgument
        assertTrue(caughtException instanceof DomainException || caughtException instanceof IllegalArgumentException,
                "Expected DomainException or IllegalArgumentException, but got: " + caughtException.getClass().getName());
    }
}
