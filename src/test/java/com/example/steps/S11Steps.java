package com.example.steps;

import com.example.domain.S11Command;
import com.example.domain.S11Event;
import com.example.domain.Transaction;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private Transaction transaction;
    private S11Command command;
    private S11Event resultEvent;
    private BigDecimal currentBalance;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        this.transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-001",
                new BigDecimal("100.00"),
                "USD"
        );
        // Setup context: Account has sufficient funds
        this.currentBalance = new BigDecimal("500.00");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled in setup, usually via context, but for simplicity we assume default valid state
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Handled in setup
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Handled in setup
    }

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        this.command = new S11Command(
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getAmount(),
                transaction.getCurrency(),
                this.currentBalance
        );
        this.resultEvent = transaction.execute(command);
    }

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        assertTrue(resultEvent instanceof S11Event.WithdrawalPosted);
        S11Event.WithdrawalPosted event = (S11Event.WithdrawalPosted) resultEvent;
        assertEquals(transaction.getId(), event.transactionId());
        assertEquals(new BigDecimal("400.00"), event.balanceAfter()); // 500 - 100
    }

    // ---- Rejection Scenarios ----

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsMustBeGreaterThanZero() {
        this.transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-001",
                new BigDecimal("-50.00"),
                "USD"
        );
        this.currentBalance = new BigDecimal("500.00");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesOncePosted() {
        this.transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-001",
                new BigDecimal("100.00"),
                "USD"
        );
        // Simulate already posted by creating a new instance in a Posted state or directly setting it
        // Since the model is simple, let's assume we have a way to force status or we use reflection/persistence.
        // However, the domain model checks status. Let's assume we retrieved an already posted one.
        // For the purpose of this test, let's cheat slightly by setting status if it were mutable,
        // or we rely on the domain logic to reject if we try to execute on an already posted transaction.
        // Since Transaction class doesn't expose a setStatus, and execute() checks it:
        // We need to ensure the aggregate behaves as if it were posted. 
        // *Correction*: The logic `if (this.status == Status.POSTED)` checks the field.
        // We will assume the test environment handles this, or we modify the Transaction class to allow creation of Posted state.
        // The simplest way without changing the API significantly is to assume the 'execute' logic handles the check.
        // But wait, `new Transaction` creates `PENDING`. How do we test the rejection?
        // We need a way to make it POSTED. Let's update the domain model to accept status in constructor for testing/factory.
        // *Self-Correction*: I will update the domain model `Transaction` to allow setting status via a factory or constructor overload, 
        // OR I will assume the test context fetches a "posted" transaction. 
        // Actually, let's look at the generated code. I'll add a `markPosted()` method to Transaction for testing hydration.
        
        // NOTE: The Domain code provided in the final output should be robust. 
        // I will add a `markPosted()` method to Transaction and use it here.
        transaction.markPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidAccountBalance() {
        this.transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-001",
                new BigDecimal("600.00"), // More than balance
                "USD"
        );
        this.currentBalance = new BigDecimal("500.00"); // Insufficient funds
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertTrue(resultEvent instanceof S11Event.TransactionRejected);
        S11Event.TransactionRejected event = (S11Event.TransactionRejected) resultEvent;
        assertNotNull(event.reason());
        assertFalse(event.reason().isEmpty());
    }
}
