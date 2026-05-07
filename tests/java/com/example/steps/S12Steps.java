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
    private TransactionReversedEvent resultEvent;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Setup a valid aggregate with some state necessary for reversal
        aggregate = new TransactionAggregate(UUID.randomUUID(), new BigDecimal("100.00"));
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_original_transaction_id_is_provided() {
        UUID originalId = UUID.randomUUID();
        // The aggregate logic will require this ID to process the reversal
        aggregate.setOriginalTransactionId(originalId);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_with_invalid_amount() {
        // An amount of 0 or negative violates the rule
        aggregate = new TransactionAggregate(UUID.randomUUID(), BigDecimal.ZERO);
        aggregate.setOriginalTransactionId(UUID.randomUUID());
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_is_already_reversed() {
        aggregate = new TransactionAggregate(UUID.randomUUID(), new BigDecimal("50.00"));
        aggregate.setOriginalTransactionId(UUID.randomUUID());
        aggregate.markAsPosted(); // Simulate state that prevents reversal (e.g. already posted/reversed)
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_causing_invalid_balance() {
        aggregate = new TransactionAggregate(UUID.randomUUID(), new BigDecimal("99999999.00"));
        aggregate.setOriginalTransactionId(UUID.randomUUID());
        aggregate.setAvailableBalance(new BigDecimal("100.00")); // Simulate insufficient balance context
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_reverse_transaction_cmd_command_is_executed() {
        try {
            command = new ReverseTransactionCmd(aggregate.getOriginalTransactionId());
            resultEvent = aggregate.execute(command);
        } catch (DomainException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        Assertions.assertNotNull(resultEvent, "Event should not be null");
        Assertions.assertEquals(aggregate.getOriginalTransactionId(), resultEvent.getOriginalTransactionId());
        Assertions.assertNull(caughtException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception to be thrown");
        Assertions.assertTrue(caughtException instanceof DomainException);
        Assertions.assertNull(resultEvent, "No event should be emitted on error");
    }
}