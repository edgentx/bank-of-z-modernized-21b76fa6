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
    private InMemoryReconciliationBatchRepository repository;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Constructor is auto-wired by Cucumber/Spring if needed, but plain new works for simple tests
    public S16Steps() {
        this.repository = new InMemoryReconciliationBatchRepository();
    }

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repository.save(aggregate);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Data setup for the command happens in the When step usually,
        // but we ensure validity here if needed.
        // For this scenario, the command created in 'When' will have valid dates.
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        aggregate = new ReconciliationBatch("batch-pending-check");
        aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        aggregate = new ReconciliationBatch("batch-entries-check");
        aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            Instant now = Instant.now();
            StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), now.minusSeconds(3600), now);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect IllegalStateException for invariant violations
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
