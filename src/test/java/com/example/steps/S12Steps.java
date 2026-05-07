package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private ReverseTransactionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate("TX-123");
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        // This step is essentially a setup for the command.
        // We'll construct the full command in the 'When' step.
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        // Default valid command for happy path or pre-error state
        cmd = new ReverseTransactionCmd(
                "TX-123",
                "TX-ORIGINAL",
                new BigDecimal("100.00"),
                "ACC-456",
                "VALID"
        );
        executeCommand();
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        TransactionReversedEvent event = (TransactionReversedEvent) resultEvents.get(0);
        assertEquals("transaction.reversed", event.type());
        assertEquals("TX-123", event.aggregateId());
    }

    // Error Scenarios

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        aggregate = new TransactionAggregate("TX-ERR-01");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_cannot_be_altered() {
        aggregate = new TransactionAggregate("TX-ERR-02");
        // Force the aggregate into a 'posted' state to trigger the invariant error.
        // We do this by executing a valid command first to set posted=true.
        ReverseTransactionCmd initialCmd = new ReverseTransactionCmd(
                "TX-ERR-02", "ORIG", BigDecimal.TEN, "ACC-123", "VALID"
        );
        aggregate.execute(initialCmd);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        aggregate = new TransactionAggregate("TX-ERR-03");
    }

    @When("the ReverseTransactionCmd command is executed with zero amount")
    public void the_ReverseTransactionCmd_command_is_executed_with_zero_amount() {
        cmd = new ReverseTransactionCmd(
                "TX-ERR-01",
                "TX-ORIGINAL",
                BigDecimal.ZERO,
                "ACC-456",
                "VALID"
        );
        executeCommand();
    }

    @When("the ReverseTransactionCmd command is executed on posted transaction")
    public void the_ReverseTransactionCmd_command_is_executed_on_posted_transaction() {
        cmd = new ReverseTransactionCmd(
                "TX-ERR-02",
                "TX-ORIGINAL",
                BigDecimal.ONE,
                "ACC-456",
                "VALID"
        );
        executeCommand();
    }

    @When("the ReverseTransactionCmd command is executed with invalid balance context")
    public void the_ReverseTransactionCmd_command_is_executed_with_invalid_balance_context() {
        cmd = new ReverseTransactionCmd(
                "TX-ERR-03",
                "TX-ORIGINAL",
                BigDecimal.ONE,
                "ACC-456",
                "INVALID" // Triggers balance validation failure
        );
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            capturedException = e;
        }
    }
}
