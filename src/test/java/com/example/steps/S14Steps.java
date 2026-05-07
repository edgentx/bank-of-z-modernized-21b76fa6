package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.CompleteTransferCmd;
import com.example.domain.transaction.model.TransferAggregate;
import com.example.domain.transaction.model.TransferCompletedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S14Steps {

    private TransferAggregate transfer;
    private CompleteTransferCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transfer aggregate")
    public void a_valid_Transfer_aggregate() {
        transfer = new TransferAggregate("tx-valid-123");
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void a_Transfer_aggregate_that_violates_source_and_destination_accounts_cannot_be_the_same() {
        transfer = new TransferAggregate("tx-invalid-same-acct");
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void a_Transfer_aggregate_that_violates_transfer_amount_must_not_exceed_the_available_balance_of_the_source_account() {
        transfer = new TransferAggregate("tx-invalid-funds");
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void a_Transfer_aggregate_that_violates_a_transfer_must_succeed_or_fail_atomically_for_both_accounts_involved() {
        transfer = new TransferAggregate("tx-invalid-atomic");
    }

    @And("a valid transferReference is provided")
    public void a_valid_transferReference_is_provided() {
        // Scenario context setup happens in the 'When' block via command construction
    }

    @When("the CompleteTransferCmd command is executed")
    public void the_CompleteTransferCmd_command_is_executed() {
        String from = "acct-123";
        String to = "acct-456";
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal balance = new BigDecimal("100.00");
        boolean atomic = true;

        if (transfer.id().equals("tx-invalid-same-acct")) {
            to = "acct-123"; // Violation: same account
        } else if (transfer.id().equals("tx-invalid-funds")) {
            balance = new BigDecimal("50.00"); // Violation: insufficient funds
        } else if (transfer.id().equals("tx-invalid-atomic")) {
            atomic = false; // Violation: atomic state failure
        }

        cmd = new CompleteTransferCmd(transfer.id(), from, to, amount, balance, atomic);

        try {
            resultEvents = transfer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transfer.completed event is emitted")
    public void a_transfer_completed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof TransferCompletedEvent);
        TransferCompletedEvent event = (TransferCompletedEvent) resultEvents.get(0);
        Assertions.assertEquals("transfer.completed", event.type());
        Assertions.assertEquals(transfer.id(), event.aggregateId());
        Assertions.assertTrue(transfer.isCompleted());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
