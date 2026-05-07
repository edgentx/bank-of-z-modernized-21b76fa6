package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private ReconciliationStartedEvent lastEvent;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repository.save(aggregate);
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // The command will be created with this window in the 'When' step.
        // This step documents the pre-condition that the window data is valid.
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_pending() {
        aggregate = new ReconciliationBatch("batch-123-violation-pending");
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        aggregate = new ReconciliationBatch("batch-123-violation-entries");
        aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            // Creating a valid window for the happy path and default usage.
            // In a real scenario, this might be parsed from the Gherkin table or context.
            Instant start = Instant.now().minusSeconds(3600);
            Instant end = Instant.now();
            
            StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), start, end);
            List events = aggregate.execute(cmd);
            
            if (!events.isEmpty()) {
                lastEvent = (ReconciliationStartedEvent) events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(lastEvent, "Expected ReconciliationStartedEvent to be emitted");
        assertEquals("reconciliation.started", lastEvent.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // The domain error is modeled as an IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
