package com.example.steps;

import com.example.domain.reconciliation.model.*;
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
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-123");
        repository.save(batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPendingPrevious() {
        batch = new ReconciliationBatch("batch-456");
        batch.markPreviousBatchPending(true);
        repository.save(batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        batch = new ReconciliationBatch("batch-789");
        batch.markEntriesUnaccounted();
        repository.save(batch);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // No action needed, command will use fixed values
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd(batch.id(), start, end);

        try {
            resultEvents = batch.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        assertEquals("reconciliation.started", event.type());
        assertEquals(batch.id(), event.aggregateId());
        
        // Verify aggregate state change
        assertEquals(ReconciliationBatch.Status.IN_PROGRESS, batch.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertNull(resultEvents, "No events should be emitted on failure");
    }
}
