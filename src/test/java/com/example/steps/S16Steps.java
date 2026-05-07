package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
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

    private ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // Context setup for the When step
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_pending() {
        aggregate = new ReconciliationBatch("batch-pending-fail");
        aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_entries_accounted() {
        aggregate = new ReconciliationBatch("batch-entries-fail");
        aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), start, end);

        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist state changes if successful
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(ReconciliationBatch.Status.STARTED, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}