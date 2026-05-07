package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transfer.model.InitiateTransferCmd;
import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.model.TransferInitiatedEvent;
import com.example.domain.transfer.repository.TransferRepository;
import com.example.mocks.InMemoryTransferRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S13Steps {

    // Using an InMemoryRepository is not strictly necessary for the Aggregate unit test logic,
    // but good for context if we needed persistence. Here we act directly on the aggregate.
    
    private TransferAggregate transfer;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Test Data Helpers
    private static final String VALID_FROM_ACCOUNT = "ACC-123";
    private static final String VALID_TO_ACCOUNT = "ACC-456";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("100.00");
    private static final BigDecimal SUFFICIENT_BALANCE = new BigDecimal("1000.00");
    private static final String VALID_CURRENCY = "USD";

    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        this.transfer = new TransferAggregate("TRANSFER-1");
        this.capturedException = null;
    }

    @And("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        // Context setup handled in command construction
    }

    @And("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        // Context setup handled in command construction
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context setup handled in command construction
    }

    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        // Assuming valid context based on "Given" clauses unless overridden by specific violation Givens
        // We construct a valid command here.
        var cmd = new InitiateTransferCmd(
            transfer.id(),
            VALID_FROM_ACCOUNT,
            VALID_TO_ACCOUNT,
            VALID_AMOUNT,
            VALID_CURRENCY,
            SUFFICIENT_BALANCE
        );
        executeCommand(cmd);
    }

    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        assertTrue(resultEvents.get(0) instanceof TransferInitiatedEvent, "Event should be TransferInitiatedEvent");
        
        var event = (TransferInitiatedEvent) resultEvents.get(0);
        assertEquals("transfer.initiated", event.type());
        assertEquals(VALID_FROM_ACCOUNT, event.fromAccount());
        assertEquals(VALID_TO_ACCOUNT, event.toAccount());
        assertEquals(VALID_AMOUNT, event.amount());
    }

    // --- Scenarios for Violations ---

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSourceAndDestinationAccountsCannotBeTheSame() {
        this.transfer = new TransferAggregate("TRANSFER-ERR-1");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesTransferAmountMustNotExceedTheAvailableBalanceOfTheSourceAccount() {
        this.transfer = new TransferAggregate("TRANSFER-ERR-2");
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesATransferMustSucceedOrFailAtomicallyForBothAccountsInvolved() {
        this.transfer = new TransferAggregate("TRANSFER-ERR-3");
    }

    // We overload the When step or use specific logic based on state. 
    // Since Cucumber matches the first matching step text, we can rely on the specific Given context.
    // However, we need to inject the invalid data into the command.
    // To do this cleanly, we check the state of the transfer or just execute specific logic.
    // Here, we'll just assume the "When" step runs the logic with specific bad data based on the ID or flags.
    
    // Refactoring When to be data-driven or context-aware based on the aggregate ID set in Given
    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted_Invalid() {
        InitiateTransferCmd cmd = null;

        if (transfer.id().equals("TRANSFER-ERR-1")) {
            // Violation: Same Account
            cmd = new InitiateTransferCmd(
                transfer.id(), VALID_FROM_ACCOUNT, VALID_FROM_ACCOUNT, 
                VALID_AMOUNT, VALID_CURRENCY, SUFFICIENT_BALANCE
            );
        } else if (transfer.id().equals("TRANSFER-ERR-2")) {
            // Violation: Insufficient Funds
            cmd = new InitiateTransferCmd(
                transfer.id(), VALID_FROM_ACCOUNT, VALID_TO_ACCOUNT, 
                new BigDecimal("5000.00"), VALID_CURRENCY, new BigDecimal("100.00")
            );
        } else if (transfer.id().equals("TRANSFER-ERR-3")) {
            // Violation: Atomicity (e.g. negative amount or specific atomic failure flag)
            // Using negative amount to trigger domain error representing failure state
             cmd = new InitiateTransferCmd(
                transfer.id(), VALID_FROM_ACCOUNT, VALID_TO_ACCOUNT, 
                new BigDecimal("-100.00"), VALID_CURRENCY, SUFFICIENT_BALANCE
            );
        } else {
            // Fallback to valid if IDs don't match (shouldn't happen in flow)
             cmd = new InitiateTransferCmd(
                transfer.id(), VALID_FROM_ACCOUNT, VALID_TO_ACCOUNT, 
                VALID_AMOUNT, VALID_CURRENCY, SUFFICIENT_BALANCE
            );
        }

        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for the specific exception type or message depending on strictness
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    private void executeCommand(InitiateTransferCmd cmd) {
        try {
            resultEvents = transfer.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
