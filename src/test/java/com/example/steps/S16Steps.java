package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
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

    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private Exception capturedException;
    private Instant batchWindow;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        this.batchWindow = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPending() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesAccounted() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), batchWindow);
            List<DomainEvent> events = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        List<DomainEvent> uncommitted = aggregate.uncommittedEvents();
        assertEquals(1, uncommitted.size());
        assertTrue(uncommitted.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) uncommitted.get(0);
        assertEquals("batch-123", event.aggregateId());
        assertEquals(batchWindow, event.batchWindow());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
