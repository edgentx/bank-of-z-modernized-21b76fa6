package com.example.steps;

import com.example.domain.transfer.model.InitiateTransferCmd;
import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.model.TransferInitiatedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S13Steps {

    private TransferAggregate transfer;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        transfer = new TransferAggregate("tx-123");
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateWithSameSourceAndDestination() {
        transfer = new TransferAggregate("tx-invalid-same");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateWithInsufficientFunds() {
        transfer = new TransferAggregate("tx-invalid-funds");
        // Set balance to 0 to ensure failure
        transfer.setContext(BigDecimal.ZERO, true, false);
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateWithAtomicityViolation() {
        transfer = new TransferAggregate("tx-invalid-atomic");
        // Set atomic lock to true to simulate failure
        transfer.setContext(new BigDecimal("1000.00"), true, true);
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        // No-op, context is handled in the execution step or setup
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        // No-op
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // No-op
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        caughtException = null;
        try {
            // For the violation scenarios, we construct specific commands to trigger the failure
            // For the success scenario, we construct valid data
            String from = "acc-1";
            String to = "acc-2";
            BigDecimal amt = new BigDecimal("100.00");

            // Adjust based on context (hacky but works for stateless steps)
            if (transfer.id().equals("tx-invalid-same")) {
                to = "acc-1"; // Force same account
            } else if (transfer.id().equals("tx-invalid-funds")) {
                amt = new BigDecimal("500.00"); // Amount > 0 (Balance set in Given)
            }

            InitiateTransferCmd cmd = new InitiateTransferCmd(
                    transfer.id(),
                    from,
                    to,
                    amt,
                    "USD"
            );

            resultEvents = transfer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransferInitiatedEvent);
        
        TransferInitiatedEvent event = (TransferInitiatedEvent) resultEvents.get(0);
        assertEquals("transfer.initiated", event.type());
        assertEquals(transfer.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Check if it's the specific exception type we expect (IllegalArgumentException or IllegalStateException)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
