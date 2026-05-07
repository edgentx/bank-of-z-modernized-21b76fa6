package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch batch;
    private final InMemoryReconciliationBatchRepository repo = new InMemoryReconciliationBatchRepository();
    private Exception caughtException;
    private Iterable<DomainEvent> resultingEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        batch = new ReconciliationBatch("batch-123");
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // Placeholder for scenario setup logic if needed
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        try {
            // Assuming we create a command instance here. Since the batchWindow is passed in the command,
            // we'll instantiate it with a valid window for the successful scenario.
            StartReconciliationCmd cmd = new StartReconciliationCmd("batch-123", java.time.Instant.now());
            resultingEvents = batch.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertTrue(resultingEvents.iterator().hasNext());
        DomainEvent event = resultingEvents.iterator().next();
        assertEquals("reconciliation.started", event.type());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_batch_pending() {
        batch = new ReconciliationBatch("batch-error-1");
        batch.markPreviousBatchPending(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("Previous batch is still pending"));
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_entries_accounted() {
        batch = new ReconciliationBatch("batch-error-2");
        batch.markEntriesUnaccounted();
    }
}
