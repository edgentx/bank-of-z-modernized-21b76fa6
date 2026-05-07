package com.example.steps;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.DepositPostedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S10Steps {

    private TransactionAggregate aggregate;
    private PostDepositCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Common Given: Valid Aggregate
    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        this.aggregate = new TransactionAggregate("txn-123");
        // Assume standard state allows posting
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Command builder pattern handles this, just noting the step is satisfied
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Command builder pattern handles this
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Command builder pattern handles this
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        try {
            // If command wasn't setup specifically by violation givens, create a valid one
            if (cmd == null) {
                cmd = new PostDepositCmd("txn-123", "acc-456", new BigDecimal("100.00"), "USD");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof DepositPostedEvent);
        DepositPostedEvent event = (DepositPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit.posted", event.type());
        Assertions.assertEquals("acc-456", event.accountNumber());
        Assertions.assertEquals(0, new BigDecimal("100.00").compareTo(event.amount()));
        Assertions.assertNull(thrownException, "Should not have thrown exception");
    }

    // ---- Violation Scenarios ----

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountMustBeGreaterThanZero() {
        this.aggregate = new TransactionAggregate("txn-invalid-amt");
        this.cmd = new PostDepositCmd("txn-invalid-amt", "acc-456", new BigDecimal("-50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesAlreadyPosted() {
        this.aggregate = new TransactionAggregate("txn-already-posted");
        // Simulate the aggregate being in a POSTED state by executing a valid command first
        PostDepositCmd initCmd = new PostDepositCmd("txn-already-posted", "acc-456", new BigDecimal("10.00"), "USD");
        aggregate.execute(initCmd);
        aggregate.markPosted(); // Internal method to simulate state change for test

        // Now try to execute the command again on the same aggregate instance
        this.cmd = new PostDepositCmd("txn-already-posted", "acc-456", new BigDecimal("20.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesAccountBalance() {
        this.aggregate = new TransactionAggregate("txn-invalid-bal");
        // In a real app, we might inject a service or set a flag on the aggregate to mock this failure.
        // For this aggregate, we will use a specific account number that triggers the mocked failure logic.
        this.cmd = new PostDepositCmd("txn-invalid-bal", "acc-blacklisted", new BigDecimal("100.00"), "USD");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}