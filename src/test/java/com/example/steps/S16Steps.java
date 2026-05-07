package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch batch;
    private Instant batchWindowStart;
    private Instant batchWindowEnd;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-001");
        caughtException = null;
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        batchWindowStart = Instant.now().minus(1, ChronoUnit.DAYS);
        batchWindowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPending() {
        batch = new ReconciliationBatch("batch-002");
        // Simulate state where previous batch is pending
        batch.markPreviousBatchPending(true);
        
        batchWindowStart = Instant.now().minus(1, ChronoUnit.DAYS);
        batchWindowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesAccounted() {
        batch = new ReconciliationBatch("batch-003");
        // Simulate state where entries are not accounted for
        batch.markEntriesUnaccounted();
        
        batchWindowStart = Instant.now().minus(1, ChronoUnit.DAYS);
        batchWindowEnd = Instant.now();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            Command cmd = new StartReconciliationCmd(batch.id(), batchWindowStart, batchWindowEnd);
            resultingEvents = batch.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(batch.id(), event.aggregateId());
        
        // Verify aggregate state mutation
        assertEquals(ReconciliationBatch.Status.IN_PROGRESS, batch.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        
        // Verify no events were emitted
        assertTrue(batch.uncommittedEvents().isEmpty(), "No events should be recorded on failure");
        
        // Verify state did not change
        assertEquals(ReconciliationBatch.Status.OPEN, batch.getStatus());
    }
}
