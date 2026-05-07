package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
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
    private Exception caughtException;
    private List events;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-123");
        repository.save(batch);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Data setup for the command happens in the When step
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPending() {
        batch = new ReconciliationBatch("batch-456");
        batch.markPreviousBatchPending(true);
        repository.save(batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesAccounting() {
        batch = new ReconciliationBatch("batch-789");
        batch.markEntriesUnaccounted();
        repository.save(batch);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            Instant start = Instant.now().minusSeconds(3600);
            Instant end = Instant.now();
            StartReconciliationCmd cmd = new StartReconciliationCmd(batch.id(), start, end);
            events = batch.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof com.example.domain.reconciliation.model.ReconciliationStartedEvent);
        
        com.example.domain.reconciliation.model.ReconciliationStartedEvent event = 
            (com.example.domain.reconciliation.model.ReconciliationStartedEvent) events.get(0);
        assertEquals("reconciliation.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // In DDD, domain rules are often enforced via IllegalStateException within the aggregate
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
