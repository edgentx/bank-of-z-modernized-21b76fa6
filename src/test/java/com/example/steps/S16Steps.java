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

    private ReconciliationBatch batch;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper for dates
    private Instant now = Instant.now();
    private Instant startWindow = now.minusSeconds(3600);
    private Instant endWindow = now;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.batch = new ReconciliationBatch("batch-001");
        repository.save(this.batch);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Start and End windows are already set to defaults
        // This step just confirms the preconditions for the scenario
        assertNotNull(startWindow);
        assertNotNull(endWindow);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWherePreviousBatchIsPending() {
        this.batch = new ReconciliationBatch("batch-002");
        // Simulate state where previous batch is pending
        this.batch.markPreviousBatchPending(true);
        repository.save(this.batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWhereEntriesAreUnaccounted() {
        this.batch = new ReconciliationBatch("batch-003");
        // Simulate state where entries are unaccounted
        this.batch.markEntriesUnaccounted();
        repository.save(this.batch);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        StartReconciliationCmd cmd = new StartReconciliationCmd(batch.id(), startWindow, endWindow);
        try {
            this.resultEvents = batch.execute(cmd);
            // Save result to simulate persistence of state changes
            repository.save(batch);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-001", event.aggregateId());
        assertEquals(ReconciliationBatch.Status.IN_PROGRESS, batch.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        
        // Verify state did not change
        assertEquals(ReconciliationBatch.Status.OPEN, batch.getStatus());
        assertNull(resultEvents);
    }
}