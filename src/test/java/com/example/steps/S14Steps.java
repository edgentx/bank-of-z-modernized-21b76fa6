package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S14Steps {

    private TransferAggregate transfer;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1: Success
    @Given("a valid Transfer aggregate")
    public void a_valid_Transfer_aggregate() {
        transfer = new TransferAggregate("tx-valid-123");
    }

    @And("a valid transferReference is provided")
    public void a_valid_transferReference_is_provided() {
        // Implicitly handled by the command data in 'When'
    }

    // Scenario 2: Source/Dest Same
    @Given("a Transfer aggregate that violates: Source and destination accounts cannot be the same.")
    public void a_Transfer_aggregate_that_violates_source_and_destination_accounts_cannot_be_the_same() {
        transfer = new TransferAggregate("tx-invalid-src-dest");
    }

    // Scenario 3: Balance Exceeded
    @Given("a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.")
    public void a_Transfer_aggregate_that_violates_balance() {
        transfer = new TransferAggregate("tx-invalid-balance");
    }

    // Scenario 4: Atomicity
    @Given("a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.")
    public void a_Transfer_aggregate_that_violates_atomicity() {
        transfer = new TransferAggregate("tx-invalid-atomic");
    }

    @When("the CompleteTransferCmd command is executed")
    public void the_CompleteTransferCmd_command_is_executed() {
        caughtException = null;
        try {
            // Construct command based on the state of the transfer ID or a default valid set
            // To make it simple, we use a builder pattern style or standard constructor
            String ref = transfer.id(); 
            
            // We determine the command parameters based on the ID prefix set in the Given steps
            // This is a bit of a hack for the Cucumber isolation, but keeps it simple
            String src = "ACC-1";
            String dest = "ACC-2";
            BigDecimal amt = new BigDecimal("100.00");
            
            if (ref.startsWith("tx-invalid-src")) {
                dest = "ACC-1"; // Same as source
            } else if (ref.startsWith("tx-invalid-bal")) {
                // The aggregate logic checks balance against a dummy/mock state
                // We'll assume the command is valid, but the aggregate checks internal state
                // For this example, we pass a negative amount to trigger balance logic if applicable, 
                // or simply rely on the aggregate knowing the balance.
                // Let's stick to the ID to determine behavior or a flag if we had one.
                // The prompt implies the aggregate "violates" the rule, meaning it's in a bad state or receiving a bad command.
                // The command handles the check. Let's pass a huge amount.
                amt = new BigDecimal("99999999.00");
            } else if (ref.startsWith("tx-invalid-atomic")) {
                // The scenario says "A transfer must succeed or fail atomically"
                // This usually implies checking state flags (e.g., debit/credit legs).
                // We'll pass a specific reference that signals this condition if needed, 
                // or assume the aggregate state handles it.
            }

            CompleteTransferCmd cmd = new CompleteTransferCmd(ref, src, dest, amt, "USD");
            resultEvents = transfer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transfer.completed event is emitted")
    public void a_transfer_completed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TransferCompletedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // In this architecture, domain violations throw exceptions (IllegalStateException/IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
