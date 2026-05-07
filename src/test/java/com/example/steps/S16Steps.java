package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatchAggregate aggregate;
    private StartReconciliationCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper constants for time windows
    private static final Instant NOW = Instant.now();
    private static final Instant ONE_HOUR_AGO = NOW.minusSeconds(3600);

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        this.aggregate = new ReconciliationBatchAggregate("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // We assume entries are accounted for in the happy path
        this.cmd = new StartReconciliationCmd(
            "batch-123", 
            "daily-001", 
            ONE_HOUR_AGO, 
            NOW, 
            true
        );
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_pending() {
        this.aggregate = new ReconciliationBatchAggregate("batch-123");
        // Manually forcing the aggregate into a state that violates the invariant
        aggregate.markAsPending();
        
        this.cmd = new StartReconciliationCmd(
            "batch-123", 
            "daily-001", 
            ONE_HOUR_AGO, 
            NOW, 
            true
        );
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_entries_accounted() {
        this.aggregate = new ReconciliationBatchAggregate("batch-123");
        
        // Command indicates entries are NOT accounted for
        this.cmd = new StartReconciliationCmd(
            "batch-123", 
            "daily-001", 
            ONE_HOUR_AGO, 
            NOW, 
            false // Simulated failure of the accounting check
        );
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        
        ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
        assertEquals("reconciliation.started", startedEvent.type());
        assertEquals("batch-123", startedEvent.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        // Check for IllegalStateException (status conflict) or IllegalArgumentException (validation)
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException), but got: " + caughtException.getClass().getSimpleName()
        );
    }
}
