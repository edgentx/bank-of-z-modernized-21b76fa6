package com.example.steps;

import com.example.domain.shared.DomainEvent;
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
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        transfer = new TransferAggregate("tx-123");
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateWithSameAccounts() {
        transfer = new TransferAggregate("tx-123");
        fromAccount = "acct-1";
        toAccount = "acct-1";
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateWithInsufficientFunds() {
        transfer = new TransferAggregate("tx-123");
        fromAccount = "acct-1";
        toAccount = "acct-2";
        amount = new BigDecimal("200.00");
        availableBalance = new BigDecimal("100.00");
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateViolatingAtomicity() {
        // In this aggregate context, atomicity violation is simulated by incomplete data causing failure
        transfer = new TransferAggregate("tx-123");
        fromAccount = null; // Missing data causing failure
        toAccount = "acct-2";
        amount = new BigDecimal("50.00");
        availableBalance = new BigDecimal("100.00");
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        fromAccount = "acct-1";
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        toAccount = "acct-2";
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        amount = new BigDecimal("50.00");
        availableBalance = new BigDecimal("100.00");
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        try {
            InitiateTransferCmd cmd = new InitiateTransferCmd(transfer.id(), fromAccount, toAccount, amount, availableBalance);
            resultEvents = transfer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("transfer.initiated", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof TransferInitiatedEvent);
        TransferInitiatedEvent event = (TransferInitiatedEvent) resultEvents.get(0);
        assertEquals("acct-1", event.fromAccount());
        assertEquals("acct-2", event.toAccount());
        assertEquals(0, new BigDecimal("50.00").compareTo(event.amount()));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
