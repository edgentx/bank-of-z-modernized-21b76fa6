package com.example.steps;

import com.example.domain.reconciliation.model.ForceBalanceCmd;
import com.example.domain.reconciliation.model.ReconciliationBalancedEvent;
import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S17Steps {

    private ReconciliationBatch batch;
    private ForceBalanceCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        this.batch = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchId is provided")
    public void a_valid_batch_id_is_provided() {
        // Handled in command construction
    }

    @Given("a valid operatorId is provided")
    public void a_valid_operator_id_is_provided() {
        // Handled in command construction
    }

    @Given("a valid justification is provided")
    public void a_valid_justification_is_provided() {
        // Handled in command construction
    }

    @When("the ForceBalanceCmd command is executed")
    public void the_force_balance_cmd_command_is_executed() {
        try {
            // Simulating command construction with valid data
            cmd = new ForceBalanceCmd("batch-123", "op-456", "Manual reconciliation required");
            resultingEvents = batch.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.balanced event is emitted")
    public void a_reconciliation_balanced_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should emit exactly one event");

        DomainEvent event = resultingEvents.get(0);
        assertInstanceOf(ReconciliationBalancedEvent.class, event, "Event type mismatch");

        ReconciliationBalancedEvent balancedEvent = (ReconciliationBalancedEvent) event;
        assertEquals("reconciliation.balanced", balancedEvent.type());
        assertEquals("batch-123", balancedEvent.batchId());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_pending_previous_batch() {
        this.batch = new ReconciliationBatch("batch-123");
        // Simulate domain state where previous batch is pending
        this.batch.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_unaccounted_entries() {
        this.batch = new ReconciliationBatch("batch-123");
        // Simulate domain state where entries are missing
        this.batch.markEntriesUnaccounted();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(thrownException.getMessage() != null && !thrownException.getMessage().isBlank(), "Error message should be present");
    }
}
