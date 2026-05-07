package com.example.steps;

import com.example.domain.reconciliation.model.*;
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

    // Test State
    private ReconciliationBatch aggregate;
    private InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // This step sets up data for the next 'When' action
        // Context implies valid Instant(s) are needed
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-123", start, end);
        
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist state changes
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Result events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-123", event.aggregateId());
        
        // Verify aggregate state transition
        assertEquals(ReconciliationBatch.Status.STARTED, aggregate.getStatus());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that violates_previous_pending() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that violates_entries_accounted() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markEntriesUnaccounted();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}