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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate(
            "txn-123",
            "acc-456",
            new BigDecimal("100.00"),
            new BigDecimal("500.00")
        );
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        aggregate = new TransactionAggregate(
            "txn-bad",
            "acc-456",
            BigDecimal.ZERO, // Violation: 0 is not > 0
            new BigDecimal("500.00")
        );
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        aggregate = new TransactionAggregate(
            "txn-posted",
            "acc-456",
            new BigDecimal("100.00"),
            new BigDecimal("500.00")
        );
        aggregate.markPosted(); // Violation: Already posted
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        aggregate = new TransactionAggregate(
            "txn-overdraft",
            "acc-456",
            new BigDecimal("600.00"), // Violation: 500 - 600 = -100 (Invalid balance)
            new BigDecimal("500.00")
        );
    }

    @And("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        // Contextual setup, usually part of the command construction in When
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        ReverseTransactionCmd cmd = new ReverseTransactionCmd("txn-123", "original-txn-999");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        
        TransactionReversedEvent event = (TransactionReversedEvent) resultEvents.get(0);
        assertEquals("transaction.reversed", event.type());
        assertEquals("txn-123", event.aggregateId());
        assertEquals("original-txn-999", event.originalTransactionId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on invariant violation type, it could be IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}