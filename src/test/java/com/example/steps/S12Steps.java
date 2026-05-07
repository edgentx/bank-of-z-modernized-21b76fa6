package com.example.steps;

import com.example.domain.S12Command;
import com.example.domain.S12Event;
import com.example.domain.Transaction;
import com.example.domain.TransactionId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S12Steps {

    private Transaction transaction;
    private TransactionId originalTransactionId;
    private S12Event resultEvent;
    private Exception domainException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        originalTransactionId = new TransactionId(UUID.randomUUID());
        transaction = new Transaction(originalTransactionId, new BigDecimal("100.00"));
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_original_transaction_id_is_provided() {
        // The ID is already initialized in the previous step for the happy path
        Assertions.assertNotNull(originalTransactionId);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        originalTransactionId = new TransactionId(UUID.randomUUID());
        // Create a transaction with zero or negative amount to trigger the invariant failure upon reversal attempt
        // Simulating a scenario where the reversal logic detects an invalid amount state
        transaction = new Transaction(originalTransactionId, BigDecimal.ZERO);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_immutability() {
        originalTransactionId = new TransactionId(UUID.randomUUID());
        // Simulate a transaction that is already reversed or locked
        transaction = new Transaction(originalTransactionId, new BigDecimal("50.00"));
        transaction.markReversed(); // Put it in a state where it cannot be reversed again
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_valid_account_balance() {
        originalTransactionId = new TransactionId(UUID.randomUUID());
        // Simulate a scenario where the transaction would cause an invalid balance
        // In a real system, this might check against the account limit. Here we flag the aggregate.
        transaction = new Transaction(originalTransactionId, new BigDecimal("999999999.00"));
        transaction.setInvalidStateFlag(true);
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_reverse_transaction_cmd_command_is_executed() {
        S12Command cmd = new S12Command(originalTransactionId);
        try {
            resultEvent = transaction.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            domainException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        Assertions.assertNotNull(resultEvent);
        Assertions.assertEquals("TransactionReversed", resultEvent.getType());
        Assertions.assertNull(domainException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(domainException);
        Assertions.assertNull(resultEvent);
    }
}
