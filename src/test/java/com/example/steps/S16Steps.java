package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
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

    // Context fields
    private ReconciliationBatch batch;
    private Instant batchWindowStart;
    private Instant batchWindowEnd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Repository (in-memory)
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-123");
        // Ensure clean state for success path
        batch.markPreviousBatchPending(false);
        // Note: areAllEntriesAccounted defaults to true
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        this.batchWindowStart = Instant.now().minusSeconds(3600);
        this.batchWindowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        batch = new ReconciliationBatch("batch-456");
        batch.markPreviousBatchPending(true); // Violate invariant
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        batch = new ReconciliationBatch("batch-789");
        batch.markEntriesUnaccounted(); // Violate invariant
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            StartReconciliationCmd cmd = new StartReconciliationCmd(
                    batch.id(),
                    this.batchWindowStart,
                    this.batchWindowEnd
            );
            resultEvents = batch.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");

        ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
        assertEquals("reconciliation.started", startedEvent.type());
        assertEquals(batch.id(), startedEvent.aggregateId());
        assertNotNull(startedEvent.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        
        // Verify it's an IllegalStateException (Domain Error)
        assertTrue(capturedException instanceof IllegalStateException, 
                "Expected IllegalStateException but got: " + capturedException.getClass().getSimpleName());
        
        // Verify no events were emitted
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }
}
