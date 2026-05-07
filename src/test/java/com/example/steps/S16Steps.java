package com.example.steps;

import com.example.domain.reconciliation.model.InMemoryReconciliationBatchRepository;
import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationBatchRepository;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private static final String BATCH_ID = "batch-001";
    private ReconciliationBatch aggregate;
    private ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        aggregate = new ReconciliationBatch(BATCH_ID);
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // State setup, handled in the When step via the command
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_batch_pending() {
        aggregate = new ReconciliationBatch(BATCH_ID);
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        aggregate = new ReconciliationBatch(BATCH_ID);
        aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        Instant now = Instant.now();
        Instant oneHourLater = now.plusSeconds(3600);
        StartReconciliationCmd cmd = new StartReconciliationCmd(BATCH_ID, now, oneHourLater);
        
        try {
            resultingEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultingEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(BATCH_ID, event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
