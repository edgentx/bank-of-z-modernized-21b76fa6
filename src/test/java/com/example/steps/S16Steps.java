package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.repository.save(this.aggregate);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Context setup usually happens in the 'When' block for command parameters in these patterns,
        // but we can define a static valid window here if needed for context.
        // We will construct the command with a valid window in the When block.
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant now = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-123", now.minusSeconds(3600), now);
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist state changes
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("reconciliation.started", resultEvents.get(0).type());
        Assertions.assertEquals("batch-123", resultEvents.get(0).aggregateId());
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousBatchPending() {
        this.aggregate = new ReconciliationBatch("batch-123-violation-pending");
        this.aggregate.markPreviousBatchPending(true);
        this.repository.save(this.aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        this.aggregate = new ReconciliationBatch("batch-123-violation-entries");
        this.aggregate.markEntriesUnaccounted();
        this.repository.save(this.aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
        Assertions.assertNull(resultEvents || resultEvents.isEmpty());
    }
}
