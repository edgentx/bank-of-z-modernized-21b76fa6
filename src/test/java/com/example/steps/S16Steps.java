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

import static org.junit.Assert.*;

public class S16Steps {

    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-1");
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // The batch window is implicitly part of the command setup
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-1", Instant.now(), Instant.now().plusSeconds(3600));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNull("Expected no exception, but got: " + caughtException, caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPreviousPending() {
        aggregate = new ReconciliationBatch("batch-2");
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        aggregate = new ReconciliationBatch("batch-3");
        aggregate.markEntriesUnaccounted();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        assertTrue("Expected IllegalStateException", caughtException instanceof IllegalStateException);
    }
}
