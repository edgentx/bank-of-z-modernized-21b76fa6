package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private final ReconciliationBatchRepository repo = new InMemoryReconciliationBatchRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new ReconciliationBatch(id);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // No specific state needed for a valid window in this context, implied by command execution
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPreviousPending() {
        aValidReconciliationBatchAggregate();
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        aValidReconciliationBatchAggregate();
        aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        // Setup the window
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        
        StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), start, end);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Result events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Result events list should not be empty");
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        
        ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
        Assertions.assertEquals(aggregate.id(), startedEvent.aggregateId());
        Assertions.assertEquals("ReconciliationStarted", startedEvent.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}