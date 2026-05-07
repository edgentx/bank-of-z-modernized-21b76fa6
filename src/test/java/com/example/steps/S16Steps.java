package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.domain.shared.Command;
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
    private final ReconciliationBatchRepository repo = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private StartReconciliationCmd cmd;
    private List resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-123");
        repo.save(batch);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // StartReconciliationCmd requires a batchWindow (e.g., a date/time range string)
        cmd = new StartReconciliationCmd("batch-123", Instant.now(), Instant.now().plusSeconds(3600));
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            // Reload to ensure clean state from repo
            var aggregate = repo.findById("batch-123").orElseThrow();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");

        var event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("batch-123", event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        batch = new ReconciliationBatch("batch-123");
        batch.markPreviousBatchPending(true);
        repo.save(batch);
        cmd = new StartReconciliationCmd("batch-123", Instant.now(), Instant.now().plusSeconds(3600));
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesUnaccounted() {
        batch = new ReconciliationBatch("batch-123");
        batch.markEntriesUnaccounted();
        repo.save(batch);
        cmd = new StartReconciliationCmd("batch-123", Instant.now(), Instant.now().plusSeconds(3600));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check it's an IllegalStateException (standard for domain invariant violations in this repo)
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}