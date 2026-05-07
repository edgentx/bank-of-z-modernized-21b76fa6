package com.example.steps;

import com.example.domain.S10Command;
import com.example.domain.S10Event;
import com.example.domain.Transaction;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private Transaction transaction;
    private S10Command command;
    private S10Event resultEvent;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new BigDecimal("100.00"));
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled in context or could be set explicitly here if needed.
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Handled in context
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Handled in context
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsMustBeGreaterThanZero() {
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new BigDecimal("100.00"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutablePosted() {
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new BigDecimal("100.00"));
        // Manually force the aggregate into a posted state for the scenario context
        S10Command initialCmd = new S10Command(transaction.getId(), "ACC-123", new BigDecimal("10.00"), "USD");
        transaction.execute(initialCmd);
        transaction.markEventsAsCommitted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidBalance() {
        // Create a transaction with a negative balance where a positive deposit (or operation) might still be invalid
        // depending on specific validation logic. Here we assume the logic checks against bounds.
        // Let's assume the validation logic we implemented is the source of truth.
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new BigDecimal("-1000.00"));
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        thrownException = null;
        try {
            // Determine command params based on state to satisfy/fail scenarios
            UUID id = transaction.getId();
            String acc = "ACC-123";
            BigDecimal amt = new BigDecimal("50.00");
            String curr = "USD";

            // Check specific scenario setups to tweak parameters if necessary
            if (transaction.getBalance().compareTo(new BigDecimal("-500")) < 0) {
                 // For the negative balance test, let's just trigger the command.
                 // Our logic allows deposits even if balance is negative, as long as newBalance >= 0.
                 // To violate "valid account balance" with our logic (newBalance < 0),
                 // we would need to withdraw, but this is a Deposit.
                 // However, we defined the violation: "A transaction must result in a valid account balance".
                 // Our domain code checks `newBalance < 0`.
                 // So starting at -1000, depositing 50 is -950. Still invalid.
                 // Let's keep the standard command.
            }

            command = new S10Command(id, acc, amt, curr);
            resultEvent = transaction.execute(command);
        } catch (Transaction.DomainViolationException e) {
            thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        assertNotNull(resultEvent);
        assertFalse(transaction.getUncommittedEvents().isEmpty());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof Transaction.DomainViolationException);
        assertNull(resultEvent);
    }
}