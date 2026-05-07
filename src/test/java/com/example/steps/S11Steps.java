package com.example.steps;

import com.example.domain.S11Command;
import com.example.domain.S11Event;
import com.example.domain.Transaction;
import com.example.domain.TransactionStatus;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;

public class S11Steps {

    // State variables for the scenario context
    private Transaction transaction;
    private S11Command command;
    private Exception caughtException;
    private S11Event resultingEvent;

    // ---------------------------------------------------------------------
    // Givens
    // ---------------------------------------------------------------------

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        // Create a new, unposted transaction
        this.transaction = new Transaction();
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        if (this.command == null) {
            this.command = new S11Command();
        }
        this.command.setAccountNumber("ACC-001-ZZ");
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        if (this.command == null) {
            this.command = new S11Command();
        }
        this.command.setAmount(new BigDecimal("100.00"));
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        if (this.command == null) {
            this.command = new S11Command();
        }
        this.command.setCurrency(Currency.getInstance("USD"));
    }

    // ---------------------------------------------------------------------
    // Negative Givens (Violations)
    // ---------------------------------------------------------------------

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsGreaterThanZero() {
        this.transaction = new Transaction();
        // Set up command with 0 or negative amount
        this.command = new S11Command();
        this.command.setAccountNumber("ACC-001-ZZ");
        this.command.setAmount(BigDecimal.ZERO); // Violation
        this.command.setCurrency(Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesCannotAlterPosted() {
        // Create a transaction that is already POSTED
        this.transaction = new Transaction();
        // Simulate that the transaction is already in a posted state (internal state mutation for test)
        // In real DDD, this would be loaded from repo as POSTED, or events applied to reach POSTED state.
        // Assuming reflection or package-private test setup, or simulating a recovered aggregate.
        // Here we assume Transaction has a way to be set to posted for invariant testing.
        
        // For this example, we assume the Transaction class allows setting status via 
        // a test accessor or we apply a mock previous event.
        // We'll simulate an existing posted transaction.
        try {
            var method = Transaction.class.getDeclaredMethod("markPostedInternal");
            method.setAccessible(true);
            method.invoke(transaction);
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed: Could not mark transaction as posted", e);
        }

        // Also ensure the command itself is valid, so the rejection is purely due to the Aggregate state
        this.command = new S11Command();
        this.command.setAccountNumber("ACC-001-ZZ");
        this.command.setAmount(new BigDecimal("50.00"));
        this.command.setCurrency(Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidAccountBalance() {
        this.transaction = new Transaction();
        // Setup: Assume the account is overdrawn or the aggregate knows the current balance is 0.
        // The aggregate logic checks (currentBalance - amount) < limit.
        
        // Set up command for a huge withdrawal
        this.command = new S11Command();
        this.command.setAccountNumber("ACC-001-ZZ");
        this.command.setAmount(new BigDecimal("99999999.00")); // Exceeds bounds
        this.command.setCurrency(Currency.getInstance("USD"));
    }

    // ---------------------------------------------------------------------
    // When
    // ---------------------------------------------------------------------

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        try {
            // Execute the command on the aggregate
            this.resultingEvent = this.transaction.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.caughtException = e;
        }
    }

    // ---------------------------------------------------------------------
    // Then
    // ---------------------------------------------------------------------

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        Assertions.assertNotNull(this.resultingEvent, "Expected an event to be emitted");
        Assertions.assertEquals("withdrawal.posted", this.resultingEvent.getType());
        Assertions.assertNotNull(this.resultingEvent.getTransactionId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(this.caughtException, "Expected a domain exception to be thrown");
        // Check it's one of our domain errors
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException,
            "Expected a valid domain exception type"
        );
    }
}
