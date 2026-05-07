package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
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
    private Exception capturedException;

    private static final String BATCH_ID = "batch-123";
    private static final Instant START = Instant.now().minusSeconds(3600);
    private static final Instant END = Instant.now();

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        batch = new ReconciliationBatch(BATCH_ID);
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // Window constants defined in class fields
    }

    @And("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_batch_pending() {
        batch = new ReconciliationBatch(BATCH_ID);
        batch.markPreviousBatchPending(true);
    }

    @And("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_unaccounted() {
        batch = new ReconciliationBatch(BATCH_ID);
        batch.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            StartReconciliationCmd cmd = new StartReconciliationCmd(BATCH_ID, START, END);
            resultEvents = batch.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(BATCH_ID, event.aggregateId());
        
        assertTrue(event instanceof ReconciliationStartedEvent);
        ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
        assertEquals(START, startedEvent.windowStart());
        assertEquals(END, startedEvent.windowEnd());

        // Verify aggregate state change
        assertEquals(ReconciliationBatch.Status.STARTED, batch.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        
        // Verify no events were emitted
        assertNull(resultEvents);
        
        // Verify aggregate state remains OPEN
        assertEquals(ReconciliationBatch.Status.OPEN, batch.getStatus());
    }
}
