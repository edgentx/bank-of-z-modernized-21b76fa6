package com.example.steps;

import com.example.domain.shared.Command;
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

    private record TransferContext(String transferId, String fromAccount, String toAccount, BigDecimal amount, BigDecimal balance) {}

    private TransferContext context;
    private TransferAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        this.context = new TransferContext("tx-123", "acct-1", "acct-2", new BigDecimal("100.00"), new BigDecimal("500.00"));
        this.aggregate = new TransferAggregate(context.transferId());
        this.aggregate.setAvailableBalance(context.balance()); // Hydrate state for validation
        this.caughtException = null;
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        // Context set in 'aValidTransferAggregate'
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        // Context set in 'aValidTransferAggregate'
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context set in 'aValidTransferAggregate'
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        try {
            Command cmd = new InitiateTransferCmd(
                context.transferId(),
                context.fromAccount(),
                context.toAccount(),
                context.amount()
            );
            resultEvents = aggregate.execute(cmd);
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
        assertEquals(context.transferId(), event.aggregateId());
        assertEquals(context.fromAccount(), event.fromAccount());
        assertEquals(context.toAccount(), event.toAccount());
        assertEquals(context.amount(), event.amount());
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        this.context = new TransferContext("tx-123", "acct-1", "acct-1", new BigDecimal("100.00"), new BigDecimal("500.00"));
        this.aggregate = new TransferAggregate(context.transferId());
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        this.context = new TransferContext("tx-123", "acct-1", "acct-2", new BigDecimal("600.00"), new BigDecimal("500.00"));
        this.aggregate = new TransferAggregate(context.transferId());
        this.aggregate.setAvailableBalance(context.balance());
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        // This rule typically involves infrastructure orSaga coordination.
        // In the aggregate, we model this as a state check: e.g., an active lock or pending transaction exists.
        this.context = new TransferContext("tx-123", "acct-1", "acct-2", new BigDecimal("100.00"), new BigDecimal("500.00"));
        this.aggregate = new TransferAggregate(context.transferId());
        // Simulate a state that violates atomicity (e.g., already processing)
        this.aggregate.setActiveLock(true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Depending on implementation, could be IllegalArgumentException, IllegalStateException, or custom
        assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException
        );
    }
}
