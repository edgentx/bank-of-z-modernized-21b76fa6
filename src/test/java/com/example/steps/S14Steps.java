package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.transaction.model.CompleteTransferCmd;
import com.example.domain.transaction.model.TransferAggregate;
import com.example.domain.transaction.model.TransferCompletedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S14Steps {

    private TransferAggregate aggregate;
    private CompleteTransferCmd cmd;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    // State helpers for specific violations
    private boolean violatesBalance = false;
    private boolean violatesAtomicity = false;
    private boolean violatesSameAccount = false;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        this.aggregate = new TransferAggregate("tx-valid-123");
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        this.aggregate = new TransferAggregate("tx-same-acct-123");
        this.violatesSameAccount = true;
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        this.aggregate = new TransferAggregate("tx-no-funds-123");
        this.violatesBalance = true;
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        this.aggregate = new TransferAggregate("tx-atomic-fail-123");
        this.violatesAtomicity = true;
    }

    @And("a valid transferReference is provided")
    public void aValidTransferReferenceIsProvided() {
        // Reference is implied by the ID, but we ensure cmd construction here if needed
    }

    @When("the CompleteTransferCmd command is executed")
    public void theCompleteTransferCmdCommandIsExecuted() {
        String from = "acct-1";
        String to = "acct-2";
        BigDecimal amount = new BigDecimal("100.00");
        String currency = "USD";

        if (violatesSameAccount) {
            to = "acct-1"; // Force violation
        }

        if (violatesBalance) {
            amount = new BigDecimal("999999999.00"); // Force Insufficient balance exception in aggregate
        }

        if (violatesAtomicity) {
            currency = "FAIL"; // Trigger atomic failure in aggregate
        }

        this.cmd = new CompleteTransferCmd(aggregate.id(), from, to, amount, currency);

        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a transfer.completed event is emitted")
    public void aTransferCompletedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransferCompletedEvent);

        TransferCompletedEvent event = (TransferCompletedEvent) resultEvents.get(0);
        assertEquals("transfer.completed", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We accept IllegalArgumentException or IllegalStateException based on the invariant
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException/IllegalStateException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
