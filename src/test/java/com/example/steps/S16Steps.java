package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch batch;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        batch = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // Context setup, mostly handled in the 'When' step via command construction
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-123", start, end);
        try {
            resultEvents = batch.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        assertEquals("reconciliation.started", resultEvents.get(0).type());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_batch_pending() {
        batch = new ReconciliationBatch("batch-123");
        batch.markPreviousBatchPending(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception should be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        assertTrue(thrownException.getMessage().contains("Previous batch is still pending"), 
                "Exception message should contain 'Previous batch is still pending'");
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_entries_accounted() {
        batch = new ReconciliationBatch("batch-123");
        batch.markEntriesUnaccounted();
    }
}