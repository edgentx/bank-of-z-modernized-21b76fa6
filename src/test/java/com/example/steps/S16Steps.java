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

    private ReconciliationBatch aggregate;
    private Instant startWindow;
    private Instant endWindow;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        this.startWindow = Instant.now();
        this.endWindow = startWindow.plusSeconds(3600);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPendingPrevious() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markPreviousBatchPending(true);
        this.startWindow = Instant.now();
        this.endWindow = startWindow.plusSeconds(3600);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markEntriesUnaccounted();
        this.startWindow = Instant.now();
        this.endWindow = startWindow.plusSeconds(3600);
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-123", startWindow, endWindow);
        try {
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultingEvents.get(0);
        assertEquals("batch-123", event.aggregateId());
        assertEquals("reconciliation.started", event.type());
        assertEquals(ReconciliationBatch.Status.IN_PROGRESS, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
