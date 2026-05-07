package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
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
    private StartReconciliationCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Throwable caughtException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // Window: Start 1 hour ago, End now
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.cmd = new StartReconciliationCmd("batch-123", start, end);
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        try {
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Throwable t) {
            this.caughtException = t;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultingEvents, "Events should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        
        ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
        assertEquals("reconciliation.started", startedEvent.type());
        assertEquals("batch-123", startedEvent.aggregateId());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_batch_pending() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markPreviousBatchPending(true);
        
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.cmd = new StartReconciliationCmd("batch-123", start, end);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(caughtException.getMessage().contains("Previous batch is still pending"), 
                "Exception message should contain invariant violation details");
        
        assertNull(resultingEvents, "No events should be emitted when command is rejected");
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_entries_accounted() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markEntriesUnaccounted();
        
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.cmd = new StartReconciliationCmd("batch-123", start, end);
    }
}