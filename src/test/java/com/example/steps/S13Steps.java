package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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

    private TransferAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // State setup helpers
    private String fromAccount = "ACC-001";
    private String toAccount = "ACC-002";
    private BigDecimal amount = new BigDecimal("100.00");
    private String currency = "USD";
    private String transferId = "TX-123";

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        this.transferId = "TX-VALID-01";
        this.aggregate = new TransferAggregate(transferId);
        // Reset defaults
        this.fromAccount = "ACC-FROM";
        this.toAccount = "ACC-TO";
        this.amount = new BigDecimal("50.00");
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        this.transferId = "TX-SAME-01";
        this.aggregate = new TransferAggregate(transferId);
        this.fromAccount = "ACC-SAME";
        this.toAccount = "ACC-SAME";
        this.amount = new BigDecimal("10.00");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        this.transferId = "TX-LOW-BAL-01";
        this.aggregate = new TransferAggregate(transferId);
        this.fromAccount = "ACC-POOR";
        this.toAccount = "ACC-RICH";
        this.amount = new BigDecimal("99999.00"); // High amount
        // Use the specific hook in the aggregate to force the invariant failure
        this.aggregate.markBalanceInsufficient();
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        this.transferId = "TX-NO-ATOM-01";
        this.aggregate = new TransferAggregate(transferId);
        this.fromAccount = "ACC-A";
        this.toAccount = "ACC-B";
        this.amount = new BigDecimal("10.00");
        // Use the specific hook to force atomicity check failure (e.g., one account missing)
        this.aggregate.markAccountsInvalidForAtomicity();
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        // Defaulted in setup, can be overridden if needed
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        // Defaulted in setup
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Defaulted in setup
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        InitiateTransferCmd cmd = new InitiateTransferCmd(transferId, fromAccount, toAccount, amount, currency);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof TransferInitiatedEvent, "Event should be TransferInitiatedEvent");
        assertEquals("transfer.initiated", event.type());
        assertEquals(transferId, event.aggregateId());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Exception should be a domain error (IllegalArgumentException or IllegalStateException)");
        
        // Verify no events were committed
        assertTrue(aggregate.uncommittedEvents().isEmpty(), "No events should be recorded if command rejected");
    }
}
