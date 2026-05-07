package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch batch;
    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        batch = new ReconciliationBatch("batch-123");
        repository.save(batch);
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // In a real framework, this might set a context variable.
        // Here we rely on the command construction in the 'When' step to be valid.
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_pending() {
        batch = new ReconciliationBatch("batch-pending-violation");
        batch.markPreviousBatchPending(true);
        repository.save(batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        batch = new ReconciliationBatch("batch-entries-violation");
        batch.markEntriesUnaccounted();
        repository.save(batch);
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        // Construct a valid command window
        Instant now = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd(batch.id(), now.minusSeconds(3600), now);

        try {
            // Reload to ensure clean state (simulated)
            ReconciliationBatch aggregate = repository.findById(batch.id()).orElseThrow();
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Save state changes
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertEquals("reconciliation.started", resultEvents.get(0).type(), "Event type mismatch");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // Verify it's the specific invariant violation (IllegalStateException)
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
