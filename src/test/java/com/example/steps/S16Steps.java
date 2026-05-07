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

    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        String batchId = "batch-123";
        aggregate = new ReconciliationBatch(batchId);
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Scenario context setup, command creation happens in 'When'
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), start, end);

        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Save state changes if any
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("reconciliation.started", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousBatchPending() {
        String batchId = "batch-pending-fail";
        aggregate = new ReconciliationBatch(batchId);
        aggregate.markPreviousBatchPending(true); // Violate invariant
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        String batchId = "batch-entries-fail";
        aggregate = new ReconciliationBatch(batchId);
        aggregate.markEntriesUnaccounted(); // Violate invariant
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
