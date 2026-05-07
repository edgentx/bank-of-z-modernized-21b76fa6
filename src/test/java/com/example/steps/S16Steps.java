package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
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
    private StartReconciliationCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.batch = new ReconciliationBatch("batch-123");
        // Ensure default state is clean for the positive case
        this.batch.markPreviousBatchPending(false);
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.command = new StartReconciliationCmd("batch-123", start, end);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            this.resultingEvents = batch.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultingEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-123", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        this.batch = new ReconciliationBatch("batch-999");
        this.batch.markPreviousBatchPending(true);
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.command = new StartReconciliationCmd("batch-999", start, end);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        this.batch = new ReconciliationBatch("batch-888");
        this.batch.markEntriesUnaccounted();
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.command = new StartReconciliationCmd("batch-888", start, end);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException but got: " + capturedException.getClass().getSimpleName());
        
        // Verify no events were emitted
        assertNull(resultingEvents);
    }
}
