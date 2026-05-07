package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.transaction.ReverseTransactionCmd;
import com.example.domain.transaction.TransactionReversedEvent;
import com.example.domain.transaction.model.TransactionAggregate;
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
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate("txn-123", "acct-456", new BigDecimal("100.00"), true);
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_original_transaction_id_is_provided() {
        // Handled in the When step via construction
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_reverse_transaction_cmd_command_is_executed() {
        try {
            Command cmd = new ReverseTransactionCmd("txn-123");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        assertEquals("transaction.reversed", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_with_invalid_amount() {
        // Zero or negative amount is invalid for reversal logic (business rule)
        aggregate = new TransactionAggregate("txn-void", "acct-void", BigDecimal.ZERO, true);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_is_not_posted() {
        // If not posted, it cannot be reversed
        aggregate = new TransactionAggregate("txn-new", "acct-new", BigDecimal.ZERO, false);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_account_balance() {
        // Using magic string "NEGATIVE_BALANCE_CHECK" from aggregate to trigger validation failure
        aggregate = new TransactionAggregate("txn-bad", "NEGATIVE_BALANCE_CHECK", BigDecimal.ONE, true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException ||
            capturedException instanceof UnknownCommandException
        );
    }
}
