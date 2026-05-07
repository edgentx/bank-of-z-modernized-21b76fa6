package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.Command;
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
    private Throwable capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Command is constructed here, but executed in the 'When' step
        Instant now = Instant.now();
        cmd = new StartReconciliationCmd("batch-123", now.minusSeconds(3600), now);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousBatchPending() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markPreviousBatchPending(true);
        Instant now = Instant.now();
        cmd = new StartReconciliationCmd("batch-123", now.minusSeconds(3600), now);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markEntriesUnaccounted();
        Instant now = Instant.now();
        cmd = new StartReconciliationCmd("batch-123", now.minusSeconds(3600), now);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertInstanceOf(ReconciliationStartedEvent.class, event, "Event must be ReconciliationStartedEvent");
        
        ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
        assertEquals("reconciliation.started", startedEvent.type());
        assertEquals("batch-123", startedEvent.aggregateId());
        
        // Verify state change on aggregate
        assertEquals(ReconciliationBatch.Status.IN_PROGRESS, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // In Java domain logic, business rule violations are typically IllegalStateException
        assertInstanceOf(IllegalStateException.class, capturedException);
        
        // Verify no events were emitted
        assertTrue(aggregate.uncommittedEvents().isEmpty() 
            || aggregate.uncommittedEvents() == null 
            || resultingEvents == null 
            || resultingEvents.isEmpty(), "No events should be emitted on failure");
    }
}
