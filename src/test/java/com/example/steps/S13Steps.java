package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transfer.model.InitiateTransferCmd;
import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.model.TransferInitiatedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S13Steps {

    private TransferAggregate aggregate;
    private InitiateTransferCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to reset state
    private void reset() {
        aggregate = null;
        cmd = null;
        resultEvents = null;
        caughtException = null;
    }

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        reset();
        aggregate = new TransferAggregate("tx-123");
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        // Context setup handled in 'When' construction or via defaults
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        // Context setup handled in 'When' construction or via defaults
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context setup handled in 'When' construction or via defaults
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        // Defaults for "Valid" scenario
        if (cmd == null) {
            cmd = new InitiateTransferCmd("tx-123", "acc-1", "acc-2", new BigDecimal("100.00"), "USD");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof TransferInitiatedEvent);
        TransferInitiatedEvent event = (TransferInitiatedEvent) resultEvents.get(0);
        Assertions.assertEquals("transfer.initiated", event.type());
        Assertions.assertEquals("tx-123", event.aggregateId());
    }

    // ---------------- Invalid Scenarios ----------------

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        reset();
        aggregate = new TransferAggregate("tx-fail-1");
        // Command with same accounts
        cmd = new InitiateTransferCmd("tx-fail-1", "acc-same", "acc-same", new BigDecimal("50.00"), "USD");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        reset();
        aggregate = new TransferAggregate("tx-fail-2");
        // Using an arbitrarily large amount to trigger the mock check in TransferAggregate
        cmd = new InitiateTransferCmd("tx-fail-2", "acc-1", "acc-2", new BigDecimal("999999999"), "USD");
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        reset();
        aggregate = new TransferAggregate("tx-fail-3");
        // Using specific currency to trigger the mock atomic failure check
        cmd = new InitiateTransferCmd("tx-fail-3", "acc-1", "acc-2", new BigDecimal("10.00"), "FAIL");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
