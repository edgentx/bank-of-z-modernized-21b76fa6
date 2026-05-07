package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S16Steps {

    private ReconciliationBatch batch;
    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-1");
        repository.save(batch);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Context setup implied, actual window is part of the command execution logic
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPreviousPending() {
        batch = new ReconciliationBatch("batch-2");
        batch.markPreviousBatchPending(true);
        repository.save(batch);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        batch = new ReconciliationBatch("batch-3");
        batch.markEntriesUnaccounted();
        repository.save(batch);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        // Retrieve the aggregate from the repository to simulate loading
        var loadedBatch = repository.findById(batch.id()).orElseThrow();

        StartReconciliationCmd cmd = new StartReconciliationCmd(
                loadedBatch.id(),
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        try {
            resultEvents = loadedBatch.execute(cmd);
            repository.save(loadedBatch); // Persist state changes if successful
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Depending on implementation, this could be IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
