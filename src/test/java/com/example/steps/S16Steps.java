package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private InMemoryReconciliationBatchRepository repository;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.aggregate = new ReconciliationBatch("batch-1");
        this.repository = new InMemoryReconciliationBatchRepository();
        repository.save(aggregate);
        this.capturedException = null;
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Logic handled in the When step via command construction
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            StartReconciliationCmd cmd = new StartReconciliationCmd("batch-1", "2023-10-01", "2023-10-31");
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                // In a real repo, we'd save here. In-memory aggregate state is updated.
            }
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        this.aggregate = new ReconciliationBatch("batch-2");
        this.repository = new InMemoryReconciliationBatchRepository();
        // Simulate violation
        aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
        this.capturedException = null;
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        this.aggregate = new ReconciliationBatch("batch-3");
        this.repository = new InMemoryReconciliationBatchRepository();
        // Simulate violation
        aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
        this.capturedException = null;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException");
    }
}
