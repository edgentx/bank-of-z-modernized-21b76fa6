package com.example.steps;

import com.example.domain.DepositPostedEvent;
import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        UUID id = UUID.randomUUID();
        this.transaction = Transaction.create(
            id,
            "ACC-001",
            BigDecimal.ZERO, // Initial amount placeholder
            "USD",
            new BigDecimal("100.00") // Current balance allows transaction
        );
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account is set in aggregate creation, just ensuring it's valid here
        assertNotNull(transaction);
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // We'll construct the command later
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // We'll construct the command later
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        // Construct a valid command based on the context
        this.command = new PostDepositCmd(
            transaction.getId(),
            "ACC-001",
            new BigDecimal("50.00"),
            "USD"
        );
        try {
            transaction.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        List<Object> events = transaction.getUncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof DepositPostedEvent, "Expected DepositPostedEvent");
    }

    // --- Negative Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountGreaterThanZero() {
        this.transaction = Transaction.create(UUID.randomUUID(), "ACC-001", BigDecimal.ZERO, "USD", BigDecimal.ZERO);
    }

    @When("the PostDepositCmd command is executed")
    public void theCmdExecutedForInvalidAmount() {
        this.command = new PostDepositCmd(
            transaction.getId(),
            "ACC-001",
            new BigDecimal("-10.00"), // Invalid
            "USD"
        );
        try {
            transaction.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutability() {
        this.transaction = Transaction.create(UUID.randomUUID(), "ACC-001", BigDecimal.ZERO, "USD", BigDecimal.ZERO);
        // Manually set status to POSTED to simulate immutable state
        // In a real repo, we would load an already posted aggregate
        transaction.execute(new PostDepositCmd(transaction.getId(), "ACC-001", BigDecimal.ONE, "USD"));
        transaction.getUncommittedEvents().clear(); // Clear previous event so we can check strictly for the failure
    }

    @When("the PostDepositCmd command is executed")
    public void theCmdExecutedForPostedTx() {
        this.command = new PostDepositCmd(
            transaction.getId(),
            "ACC-001",
            new BigDecimal("20.00"),
            "USD"
        );
        try {
            transaction.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalanceValidation() {
        // Current balance is very high, next deposit exceeds limit (see Transaction logic)
        this.transaction = Transaction.create(
            UUID.randomUUID(), 
            "ACC-001", 
            BigDecimal.ZERO, 
            "USD", 
            new BigDecimal("999999.00") // Balance that will overflow with the test deposit
        );
    }

    @When("the PostDepositCmd command is executed")
    public void theCmdExecutedForInvalidBalance() {
        this.command = new PostDepositCmd(
            transaction.getId(),
            "ACC-001",
            new BigDecimal("50.00"),
            "USD"
        );
        try {
            transaction.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof Transaction.DomainViolationException, 
            "Expected DomainViolationException but got: " + caughtException.getClass().getSimpleName());
    }
}
