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
    
    private TransferAggregate aggregate;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Transfer aggregate")
    public void a_valid_Transfer_aggregate() {
        aggregate = new TransferAggregate("tx-123");
        fromAccount = "acc-1";
        toAccount = "acc-2";
        amount = new BigDecimal("100.00");
        availableBalance = new BigDecimal("500.00");
    }

    @And("a valid fromAccount is provided")
    public void a_valid_fromAccount_is_provided() {
        // Default setup in a_valid_Transfer_aggregate
    }

    @And("a valid toAccount is provided")
    public void a_valid_toAccount_is_provided() {
        // Default setup in a_valid_Transfer_aggregate
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Default setup in a_valid_Transfer_aggregate
    }

    @When("the InitiateTransferCmd command is executed")
    public void the_InitiateTransferCmd_command_is_executed() {
        try {
            InitiateTransferCmd cmd = new InitiateTransferCmd(
                aggregate.id(),
                fromAccount,
                toAccount,
                amount,
                "USD",
                availableBalance
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transfer.initiated event is emitted")
    public void a_transfer_initiated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransferInitiatedEvent);
        
        TransferInitiatedEvent event = (TransferInitiatedEvent) resultEvents.get(0);
        assertEquals("transfer.initiated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void a_Transfer_aggregate_that_violates_Source_and_destination_accounts_cannot_be_the_same() {
        aggregate = new TransferAggregate("tx-invalid-1");
        fromAccount = "acc-1";
        toAccount = "acc-1"; // Same account
        amount = new BigDecimal("100.00");
        availableBalance = new BigDecimal("500.00");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void a_Transfer_aggregate_that_violates_Transfer_amount_must_not_exceed_the_available_balance() {
        aggregate = new TransferAggregate("tx-invalid-2");
        fromAccount = "acc-1";
        toAccount = "acc-2";
        amount = new BigDecimal("600.00"); // Exceeds balance
        availableBalance = new BigDecimal("500.00");
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void a_Transfer_aggregate_that_violates_atomicity() {
        aggregate = new TransferAggregate("tx-invalid-3");
        fromAccount = null; // Violates atomicity precondition (cannot complete without source)
        toAccount = "acc-2";
        amount = new BigDecimal("100.00");
        availableBalance = new BigDecimal("500.00");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
