package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch batch;
    private StartReconciliationCmd cmd;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-1");
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPendingPrevious() {
        batch = new ReconciliationBatch("batch-2");
        batch.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        batch = new ReconciliationBatch("batch-3");
        batch.markEntriesUnaccounted();
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Instantiation of command happens in the When step
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant start = Instant.parse("2023-01-01T00:00:00Z");
        Instant end = Instant.parse("2023-01-01T23:59:59Z");
        cmd = new StartReconciliationCmd(batch.id(), start, end);

        try {
            resultEvents = batch.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("reconciliation.started", resultEvents.get(0).type());
        assertEquals(batch.id(), resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
