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

    private ReconciliationBatch aggregate;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Parameters for the command are set in the 'When' step, or we could store them in context.
        // For simplicity, we construct the command in the 'When' step using fixed valid data.
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        this.aggregate = new ReconciliationBatch("batch-xyz");
        this.aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        this.aggregate = new ReconciliationBatch("batch-abc");
        this.aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant now = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd(
            aggregate.id(),
            now.minusSeconds(3600),
            now
        );

        try {
            // In a real CQRS setup, we might go through a command handler, but testing the aggregate directly is valid for Unit tests.
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist state changes
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent, "Expected ReconciliationStartedEvent");
        assertNull(capturedException, "Expected no exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        assertNull(resultEvents, "Expected no events to be emitted when command is rejected");
    }
}
