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

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd cmd;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        // Setup a standard valid transaction context
        transaction = new Transaction("tx-123", "ACC-001", BigDecimal.valueOf(1000.00));
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Field is part of the Command, initialized in the When clause usually,
        // but we ensure we use a valid one in the positive flow.
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Same as above, handled in When.
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Same as above, handled in When.
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        // Construct a valid command for the happy path
        if (cmd == null) {
            cmd = new PostDepositCmd("ACC-001", BigDecimal.valueOf(500.00), "USD");
        }
        try {
            transaction.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        List<Object> events = transaction.getUncommittedEvents();
        assertFalse(events.isEmpty(), "Expected an event to be emitted");
        assertEquals(1, events.size(), "Expected exactly one event");
        assertTrue(events.get(0) instanceof DepositPostedEvent, "Expected DepositPostedEvent");

        DepositPostedEvent event = (DepositPostedEvent) events.get(0);
        assertEquals("tx-123", event.transactionId());
        assertEquals(BigDecimal.valueOf(500.00), event.amount());
        assertEquals("USD", event.currency());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsMustBeGreaterThanZero() {
        transaction = new Transaction("tx-invalid-amount", "ACC-001", BigDecimal.valueOf(100.00));
        cmd = new PostDepositCmd("ACC-001", BigDecimal.ZERO, "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesOncePosted() {
        transaction = new Transaction("tx-already-posted", "ACC-001", BigDecimal.valueOf(100.00));
        // Manually post it to set the state
        transaction.execute(new PostDepositCmd("ACC-001", BigDecimal.TEN, "USD"));
        transaction.clearEvents(); // Clear setup events

        // Now try to post again (or command the same aggregate)
        cmd = new PostDepositCmd("ACC-001", BigDecimal.valueOf(50.00), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidBalance() {
        // Balance is 0, trying to post a negative amount or spend too much?
        // The invariant "Transaction amounts must be > 0" handles negative deposits.
        // But here we check the "Valid Account Balance" logic (e.g. Overdraft protection).
        // If the logic allows negative balances via withdrawal, that's different.
        // But for Deposit, a valid balance violation might be exotic.
        // Let's assume the validation logic implies that *any* transaction resulting in invalid state fails.
        // Setup: Balance 100. Deposit -200 (violates amount > 0).
        // Let's assume the invariant is strictly about resulting balance.
        // Since Deposit *adds* money, it rarely makes a balance invalid unless overflow or specific business rule.
        // However, based on the prompt, we just need to trigger the "valid account balance" error.
        // We can simulate this by checking if the aggregate has logic that rejects specific transactions.
        // To satisfy the specific scenario:
        transaction = new Transaction("tx-balance-check", "ACC-001", BigDecimal.valueOf(-100));
        // If we deposit a small amount, still invalid? No, sum is -90.
        // Let's stick to the text. The aggregate validates the resulting balance.
        // I will assume a specific rule: Balance must remain positive.
        transaction = new Transaction("tx-low-balance", "ACC-001", BigDecimal.valueOf(-10.00));
        cmd = new PostDepositCmd("ACC-001", BigDecimal.valueOf(5.00), "USD");
        // Resulting balance -5. Invalid.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a DomainException to be thrown");
        assertTrue(caughtException instanceof Transaction.DomainException, "Expected Transaction.DomainException");
    }
}