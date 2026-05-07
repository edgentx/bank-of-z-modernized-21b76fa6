package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.Command;
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
    private Instant windowStart;
    private Instant windowEnd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        batch = new ReconciliationBatch("batch-001");
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        windowStart = Instant.now().minusSeconds(3600);
        windowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        batch = new ReconciliationBatch("batch-002");
        batch.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        batch = new ReconciliationBatch("batch-003");
        batch.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Command cmd = new StartReconciliationCmd(batch.id(), windowStart, windowEnd);
        try {
            resultingEvents = batch.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        DomainEvent event = resultingEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(batch.id(), event.aggregateId());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertNull(resultingEvents, "No events should be produced on failure");
    }
}
