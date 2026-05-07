package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class S16Steps {

    private ReconciliationBatch batch;
    private final InMemoryReconciliationBatchRepository repo = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-123");
        repo.save(batch);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Context setup: implied by command creation in When step
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            // Reload to ensure we are testing persistence state (optional but good practice)
            ReconciliationBatch aggregate = repo.findById("batch-123").orElseThrow();
            
            Command cmd = new StartReconciliationCmd(
                "batch-123", 
                Instant.now(), 
                Instant.now().plusSeconds(3600), 
                "op-1"
            );
            
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        batch = new ReconciliationBatch("batch-violation-1");
        batch.markPreviousBatchPending(true);
        repo.save(batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        batch = new ReconciliationBatch("batch-violation-2");
        batch.markEntriesUnaccounted();
        repo.save(batch);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // In Java, domain errors are typically IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException
        );
    }
}