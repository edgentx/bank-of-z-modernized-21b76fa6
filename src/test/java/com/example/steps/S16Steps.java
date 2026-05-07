package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    // Reusing the in-memory repository pattern established in S10/S17
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;
    private String batchWindow;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        String batchId = "batch-123";
        this.aggregate = new ReconciliationBatch(batchId);
        repository.save(this.aggregate);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        this.batchWindow = "2023-10-27";
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousPending() {
        aValidReconciliationBatchAggregate();
        aValidBatchWindowIsProvided();
        // Simulate the invariant violation: a previous batch is pending
        this.aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        aValidReconciliationBatchAggregate();
        aValidBatchWindowIsProvided();
        // Simulate the invariant violation: entries are missing
        this.aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            StartReconciliationCmd cmd = new StartReconciliationCmd(
                    this.aggregate.id(),
                    this.batchWindow,
                    Instant.now()
            );
            this.resultingEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(this.resultingEvents, "Events should not be null");
        assertEquals(1, this.resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = this.resultingEvents.get(0);
        assertEquals("reconciliation.started", event.type(), "Event type should be reconciliation.started");
        assertEquals(this.aggregate.id(), event.aggregateId(), "Aggregate ID should match");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.caughtException, "An exception should have been thrown");
        assertTrue(this.caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }
}
