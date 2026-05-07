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

    private ReconciliationBatch aggregate;
    private InMemoryReconciliationBatchRepository repo = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repo.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPendingPreviousBatch() {
        aggregate = new ReconciliationBatch("batch-123");
        // Simulate loading from history where previous batch is pending
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        aggregate = new ReconciliationBatch("batch-123");
        // Simulate loading from history where entries are missing
        aggregate.markEntriesUnaccounted();
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // No-op, context handled in When step via command construction
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        StartReconciliationCmd cmd = new StartReconciliationCmd(
                "batch-123",
                Instant.now().minusSeconds(3600),
                Instant.now(),
                "operator-1"
        );
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-123", event.aggregateId());
        assertEquals(ReconciliationBatch.Status.STARTED, aggregate.getStatus());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertNull(resultEvents);
    }
}
