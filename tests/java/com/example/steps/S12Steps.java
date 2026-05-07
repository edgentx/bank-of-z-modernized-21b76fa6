package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S12Steps {

    private TransactionAggregate aggregate;
    private ReverseTransactionCmd command;
    private TransactionReversedEvent resultingEvent;
    private DomainException caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        UUID id = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        // Initialize a valid aggregate state
        this.aggregate = new TransactionAggregate(id, amount);
        // Pre-condition: Assume posted to allow reversal logic validation
        this.aggregate.markAsPosted(); 
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        UUID transactionId = UUID.randomUUID();
        this.command = new ReverseTransactionCmd(transactionId);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_positive() {
        // Create aggregate with invalid amount (e.g., zero or negative) to test invariant enforcement
        this.aggregate = new TransactionAggregate(UUID.randomUUID(), BigDecimal.ZERO);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutable_posted_rule() {
        // Simulate a scenario where the reversal logic is blocked, e.g., trying to reverse an already reversed transaction
        // or logic inside execute prevents reversal based on specific state.
        this.aggregate = new TransactionAggregate(UUID.randomUUID(), BigDecimal.ONE);
        this.aggregate.markAsPosted();
        this.aggregate.markAsReversed(); // Making it invalid for a second reversal
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        // Setup aggregate where reversal would cause invalid balance (e.g., overdraft)
        this.aggregate = new TransactionAggregate(UUID.randomUUID(), new BigDecimal("1000.00"));
        // Mock state where current balance is 0, reversing a 1000 credit causes overdraft
        this.aggregate.markAsPosted();
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        try {
            // Execute command on the aggregate
            resultingEvent = aggregate.execute(command);
        } catch (DomainException e) {
            caughtException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent, "Event should not be null");
        Assertions.assertNotNull(resultingEvent.getTransactionId(), "Event must contain ID");
        Assertions.assertNull(caughtException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "DomainException should have been thrown");
        Assertions.assertNull(resultingEvent, "No event should be emitted on failure");
    }
}
