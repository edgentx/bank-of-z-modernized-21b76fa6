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

    private ReconciliationBatch aggregate;
    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<?> resultEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.aggregate = new ReconciliationBatch("batch-001");
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // No-op, just ensuring the context is ready.
        // Actual times are constructed in the When step.
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-001", start, end);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertEquals("reconciliation.started", resultEvents.get(0).getClass().getSimpleName()); 
        // Note: ReconciliationStartedEvent is a record, getClass().getSimpleName() returns the record name.
        // Or check specific type: assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        this.aggregate = new ReconciliationBatch("batch-002");
        this.aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        this.aggregate = new ReconciliationBatch("batch-003");
        this.aggregate.markEntriesUnaccounted();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Should have thrown an exception");
        assertTrue(thrownException instanceof IllegalStateException, "Should be an IllegalStateException domain error");
    }
}
