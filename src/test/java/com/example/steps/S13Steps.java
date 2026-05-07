package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.transfer.InitiateTransferCmd;
import com.example.domain.transfer.TransferAggregate;
import com.example.domain.transfer.TransferInitiatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S13Steps {

    private TransferAggregate aggregate;
    private Exception capturedException;
    private List<DomainException> events;

    // Scenario 1: Success
    @Given("a valid Transfer aggregate")
    public void aValidTransferAggregate() {
        aggregate = new TransferAggregate("tx-123");
    }
    @Given("a valid fromAccount is provided")
    public void aValidFromAccountIsProvided() {
        // Context placeholder
    }
    @Given("a valid toAccount is provided")
    public void aValidToAccountIsProvided() {
        // Context placeholder
    }
    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context placeholder
    }
    @When("the InitiateTransferCmd command is executed")
    public void theInitiateTransferCmdCommandIsExecuted() {
        // Assuming default valid data based on context setup in previous steps
        // or explicitly setting it here for the valid scenario
        InitiateTransferCmd cmd = new InitiateTransferCmd("tx-123", "acc-1", "acc-2", new BigDecimal("100.00"));
        try {
            var events = aggregate.execute(cmd);
            this.events = events;
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
    @Then("a transfer.initiated event is emitted")
    public void aTransferInitiatedEventIsEmitted() {
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof TransferInitiatedEvent);
    }

    // Scenario 2: Same Account
    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void aTransferAggregateThatViolatesSameAccount() {
        aggregate = new TransferAggregate("tx-456");
        // Prepare command state for execution
        this.storedCmd = new InitiateTransferCmd("tx-456", "acc-1", "acc-1", new BigDecimal("50.00"));
    }
    
    // Scenario 3: Insufficient Funds
    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void aTransferAggregateThatViolatesInsufficientFunds() {
        aggregate = new TransferAggregate("tx-789");
        // Amount exceeds balance logic is inside the aggregate, we pass a large amount
        this.storedCmd = new InitiateTransferCmd("tx-789", "acc-1", "acc-2", new BigDecimal("9999999.00"));
    }

    // Scenario 4: Atomicity
    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void aTransferAggregateThatViolatesAtomicity() {
        aggregate = new TransferAggregate("tx-101");
        // We simulate a failure condition (e.g. system status)
        this.storedCmd = new InitiateTransferCmd("tx-101", "acc-1", "acc-2", new BigDecimal("10.00"));
        // We'll mark the aggregate or command to trigger this specific failure
    }

    // Common Then for errors
    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Helper field to carry command from Given (violation setup) to When (execution)
    private InitiateTransferCmd storedCmd;

    @When("the InitiateTransferCmd command is executed")
    public void executeStoredCommand() {
        if (storedCmd != null) {
            try {
                aggregate.execute(storedCmd);
            } catch (IllegalArgumentException e) {
                capturedException = e;
            }
        }
    }
}
