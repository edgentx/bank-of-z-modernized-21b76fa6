package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transfer.model.CompleteTransferCmd;
import com.example.domain.transfer.model.TransferAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S14Steps {

    private TransferAggregate transfer;
    private String transferReference;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal transferAmount;
    private BigDecimal sourceAccountBalance;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transfer aggregate")
    public void a_valid_transfer_aggregate() {
        transferReference = "TRF-12345";
        transfer = new TransferAggregate(transferReference);
    }

    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void a_transfer_aggregate_that_violates_source_and_destination_accounts_cannot_be_the_same() {
        transferReference = "TRF-SAME-ACCT";
        sourceAccountId = "ACCT-1";
        destinationAccountId = "ACCT-1"; // Violation
        transferAmount = new BigDecimal("100.00");
        sourceAccountBalance = new BigDecimal("1000.00");
        transfer = new TransferAggregate(transferReference);
    }

    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void a_transfer_aggregate_that_violates_transfer_amount_must_not_exceed_the_available_balance_of_the_source_account() {
        transferReference = "TRF-INSUFF-FUNDS";
        sourceAccountId = "ACCT-1";
        destinationAccountId = "ACCT-2";
        transferAmount = new BigDecimal("500.00");
        sourceAccountBalance = new BigDecimal("100.00"); // Violation: Amount > Balance
        transfer = new TransferAggregate(transferReference);
    }

    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void a_transfer_aggregate_that_violates_a_transfer_must_succeed_or_fail_atomically_for_both_accounts_involved() {
        // This context implies a situation where partial success might occur (e.g. DB inconsistency)
        // In our Aggregate logic, we enforce invariants upfront.
        // We'll simulate a scenario that would typically break atomicity by failing validation.
        transferReference = "TRF-ATOMIC";
        sourceAccountId = "ACCT-1";
        destinationAccountId = "ACCT-2";
        transferAmount = new BigDecimal("100.00");
        sourceAccountBalance = new BigDecimal("50.00"); // Violates balance, causing atomic failure
        transfer = new TransferAggregate(transferReference);
    }

    @And("a valid transferReference is provided")
    public void a_valid_transfer_reference_is_provided() {
        // Reference is set in 'Given a valid Transfer aggregate'
        assertNotNull(transferReference);
    }

    @When("the CompleteTransferCmd command is executed")
    public void the_complete_transfer_cmd_command_is_executed() {
        try {
            // Defaults for the happy path setup
            if (sourceAccountId == null) sourceAccountId = "ACCT-SOURCE";
            if (destinationAccountId == null) destinationAccountId = "ACCT-DEST";
            if (transferAmount == null) transferAmount = new BigDecimal("10.00");
            if (sourceAccountBalance == null) sourceAccountBalance = new BigDecimal("1000.00");

            Command cmd = new CompleteTransferCmd(
                    transferReference,
                    sourceAccountId,
                    destinationAccountId,
                    transferAmount,
                    sourceAccountBalance
            );
            resultEvents = transfer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transfer.completed event is emitted")
    public void a_transfer_completed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("transfer.completed", resultEvents.get(0).type());
        assertTrue(transfer.isCompleted());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
        assertNull(resultEvents);
        assertFalse(transfer.isCompleted());
    }
}
