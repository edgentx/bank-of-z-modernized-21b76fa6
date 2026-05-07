package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.CompleteTransferCmd;
import com.example.domain.transaction.model.TransferAggregate;
import com.example.domain.transaction.model.TransferCompletedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S14Steps {

    private TransferAggregate transfer;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        transfer = new TransferAggregate("tx-valid-123");
        transfer.configure(
                "acct-001",
                "acct-002",
                new BigDecimal("100.00"),
                "USD",
                new BigDecimal("500.00"),
                true
        );
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateWithSameAccounts() {
        transfer = new TransferAggregate("tx-same-act");
        transfer.configure(
                "acct-001",
                "acct-001",
                new BigDecimal("100.00"),
                "USD",
                new BigDecimal("500.00"),
                true
        );
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateWithInsufficientFunds() {
        transfer = new TransferAggregate("tx-no-funds");
        transfer.configure(
                "acct-001",
                "acct-002",
                new BigDecimal("600.00"),
                "USD",
                new BigDecimal("500.00"),
                true
        );
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatIsNotAtomic() {
        transfer = new TransferAggregate("tx-not-atomic");
        transfer.configure(
                "acct-001",
                "acct-002",
                new BigDecimal("100.00"),
                "USD",
                new BigDecimal("500.00"),
                false // Violates atomicity invariant
        );
    }

    @And("a valid transferReference is provided")
    public void aValidTransferReferenceIsProvided() {
        // Transfer ID is set during construction in the Given steps
    }

    @When("the CompleteTransferCmd command is executed")
    public void theCompleteTransferCmdCommandIsExecuted() {
        try {
            CompleteTransferCmd cmd = new CompleteTransferCmd(transfer.id());
            resultEvents = transfer.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            thrownException = e;
        }
    }

    @Then("a transfer.completed event is emitted")
    public void aTransferCompletedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransferCompletedEvent);
        TransferCompletedEvent event = (TransferCompletedEvent) resultEvents.get(0);
        assertEquals("transfer.completed", event.type());
        assertEquals(transfer.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
