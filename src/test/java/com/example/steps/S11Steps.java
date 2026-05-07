package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Step Definitions for S-11: PostWithdrawalCmd.
 * Uses in-memory aggregates for verification without database interaction.
 */
public class S11Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private S11Event resultingEvent;
    private Exception capturedException;

    // Helper to create a fresh, valid transaction
    private Transaction createValidTransaction() {
        return new Transaction(UUID.randomUUID(), "ACC-123", new BigDecimal("1000.00"), "USD", TransactionStatus.PENDING);
    }

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        this.transaction = createValidTransaction();
        this.accountNumber = "ACC-123";
        this.amount = new BigDecimal("50.00");
        this.currency = "USD";
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Setup in previous step
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Setup in previous step
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Setup in previous step
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.transaction = createValidTransaction();
        this.accountNumber = "ACC-123";
        this.amount = new BigDecimal("-10.00"); // Invalid amount
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        // Create a transaction that is already POSTED
        this.transaction = new Transaction(UUID.randomUUID(), "ACC-123", new BigDecimal("1000.00"), "USD", TransactionStatus.POSTED);
        this.accountNumber = "ACC-123";
        this.amount = new BigDecimal("50.00");
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_must_result_in_valid_balance() {
        // Create a transaction with a balance of 10.00
        this.transaction = new Transaction(UUID.randomUUID(), "ACC-999", new BigDecimal("10.00"), "USD", TransactionStatus.PENDING);
        this.accountNumber = "ACC-999";
        this.amount = new BigDecimal("100.00"); // Trying to withdraw more than available
        this.currency = "USD";
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        try {
            S11Command command = new S11Command(accountNumber, amount, currency);
            resultingEvent = transaction.execute(command);
        } catch (DomainException e) {
            this.capturedException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent, "Expected a S11Event to be emitted");
        Assertions.assertEquals("ACC-123", resultingEvent.getAccountNumber());
        Assertions.assertEquals(new BigDecimal("50.00"), resultingEvent.getAmount());
        Assertions.assertEquals(TransactionStatus.POSTED, resultingEvent.getStatus());
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a DomainException to be thrown");
        Assertions.assertTrue(capturedException instanceof DomainException);
        Assertions.assertNull(resultingEvent, "Expected no event to be emitted on failure");
    }
}
