package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.TransactionReversedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S12Steps {

    private TransactionAggregate aggregate;
    private ReverseTransactionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = new TransactionAggregate("TXN-123");
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_original_transaction_id_is_provided() {
        // Context handled in 'When' construction for simplicity, or stored here
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_reverse_transaction_cmd_command_is_executed() {
        executeCommand(new BigDecimal("100.00"));
    }

    private void executeCommand(BigDecimal amount) {
        try {
            // Assuming a valid ID is needed if not specified in the Given
            String originalId = "ORIG-TXN-999"; 
            command = new ReverseTransactionCmd("TXN-123", originalId, amount);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        TransactionReversedEvent event = (TransactionReversedEvent) resultEvents.get(0);
        assertEquals("transaction.reversed", event.type());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_transaction_amounts_must_be_greater_than_zero() {
        aggregate = new TransactionAggregate("TXN-INVALID-AMT");
    }

    @When("the ReverseTransactionCmd command is executed with invalid amount")
    public void the_reverse_transaction_cmd_command_is_executed_with_invalid_amount() {
        executeCommand(BigDecimal.ZERO);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_transactions_cannot_be_altered_or_deleted_once_posted() {
        aggregate = new TransactionAggregate("TXN-ALREADY-POSTED");
        aggregate.markAsPosted(); // Simulate that this transaction is already posted
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_a_transaction_must_result_in_a_valid_account_balance() {
        aggregate = new TransactionAggregate("TXN-INVALID-BAL");
    }

    @When("the ReverseTransactionCmd command is executed with overdraft amount")
    public void the_reverse_transaction_cmd_command_is_executed_with_overdraft_amount() {
        // Aggregate rule: > 10000 triggers invalid balance error
        executeCommand(new BigDecimal("10001.00"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Check for specific error types or messages if necessary, but generic Exception/IllegalStateException fits
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
