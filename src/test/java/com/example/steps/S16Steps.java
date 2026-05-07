package com.example.steps;

import com.example.domain.reconciliation.model.InMemoryReconciliationBatchRepository;
import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationBatchRepository;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private ReconciliationBatchRepository repository;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private Instant batchWindowStart;
    private Instant batchWindowEnd;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repository = new InMemoryReconciliationBatchRepository();
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        this.batchWindowStart = Instant.now().minusSeconds(3600);
        this.batchWindowEnd = Instant.now();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            StartReconciliationCmd cmd = new StartReconciliationCmd(
                    aggregate.id(),
                    batchWindowStart,
                    batchWindowEnd
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-123", event.aggregateId());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_batch_pending() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markEntriesUnaccounted();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}