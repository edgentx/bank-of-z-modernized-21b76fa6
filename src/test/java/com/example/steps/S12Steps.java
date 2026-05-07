package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.TransactionReversedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S12Steps {

    private TransactionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        aggregate = new TransactionAggregate("tx-123");
        // Setup a state that is valid for reversal
        // Assume a previous posting of 100.00 occurred, so current balance is 100.00
        aggregate.configureForTest(new BigDecimal("100.00"), true, false, new BigDecimal("100.00"));
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmounts() {
        aggregate = new TransactionAggregate("tx-invalid-amount");
        aggregate.configureForTest(BigDecimal.ZERO, true, false, BigDecimal.ZERO);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutability() {
        aggregate = new TransactionAggregate("tx-immutable");
        // Setup as already reversed to trigger the immutability check
        aggregate.configureForTest(new BigDecimal("50.00"), true, true, new BigDecimal("50.00"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalanceValidation() {
        aggregate = new TransactionAggregate("tx-invalid-balance");
        // Balance is 10.00, trying to reverse 100.00 would result in -90.00
        aggregate.configureForTest(new BigDecimal("100.00"), true, false, new BigDecimal("10.00"));
    }

    @And("a valid originalTransactionId is provided")
    public void aValidOriginalTransactionIdIsProvided() {
        // Data setup helper, the actual ID is used in the When step
        // No-op, just readability
    }

    @When("the ReverseTransactionCmd command is executed")
    public void theReverseTransactionCmdCommandIsExecuted() {
        try {
            // We construct the command based on the scenario context
            // For standard valid case
            String originalTxId = "orig-tx-1";
            BigDecimal amount = new BigDecimal("100.00");

            // Override based on setup state if necessary for specific negative cases
            if (aggregate.getCurrentAccountBalance().compareTo(new BigDecimal("10.00")) == 0) {
                amount = new BigDecimal("100.00"); // Force overdraft
            } else if (aggregate.getCurrentAccountBalance().compareTo(BigDecimal.ZERO) == 0) {
                amount = BigDecimal.ZERO; // Force zero amount
            }

            Command cmd = new ReverseTransactionCmd(aggregate.id(), originalTxId, amount);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void aTransactionReversedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        assertEquals("transaction.reversed", resultEvents.get(0).type());
        assertEquals(aggregate.id(), resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // In Java domain, domain errors are often modeled as Exceptions (IllegalArgumentException, IllegalStateException)
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
