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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S13Steps {

    private TransferAggregate aggregate;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        aggregate = new TransferAggregate(UUID.randomUUID().toString());
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        fromAccount = "ACC-123";
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        toAccount = "ACC-456";
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        amount = new BigDecimal("100.00");
    }

    // Scenarios for violations require specific setup in the "Given" clauses
    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        aggregate = new TransferAggregate(UUID.randomUUID().toString());
        fromAccount = "ACC-SAME";
        toAccount = "ACC-SAME";
        amount = new BigDecimal("50.00");
        availableBalance = new BigDecimal("1000.00");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        aggregate = new TransferAggregate(UUID.randomUUID().toString());
        fromAccount = "ACC-123";
        toAccount = "ACC-456";
        amount = new BigDecimal("500.00");
        availableBalance = new BigDecimal("100.00"); // Balance is lower than amount
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        // The aggregate enforces atomicity via exceptions. 
        // This setup triggers an invariant failure (like insufficient funds) which fails atomically.
        aggregate = new TransferAggregate(UUID.randomUUID().toString());
        fromAccount = "ACC-123";
        toAccount = "ACC-456";
        amount = new BigDecimal("9999.00");
        availableBalance = new BigDecimal("0.00");
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        try {
            // Setup default balance for the happy path if not set in previous steps
            if (availableBalance == null) {
                availableBalance = amount.add(new BigDecimal("1000.00"));
            }
            
            var cmd = new InitiateTransferCmd(fromAccount, toAccount, amount, availableBalance);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransferInitiatedEvent);
        
        TransferInitiatedEvent event = (TransferInitiatedEvent) resultEvents.get(0);
        assertEquals("transfer.initiated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(fromAccount, event.fromAccount());
        assertEquals(toAccount, event.toAccount());
        assertEquals(0, event.amount().compareTo(amount)); // Compare BigDecimal value
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        
        // Verify no events were emitted due to atomicity failure
        assertNull(resultEvents);
    }
}
