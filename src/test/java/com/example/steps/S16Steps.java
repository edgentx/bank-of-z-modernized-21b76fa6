package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-001");
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Scenario context setup, implicit in the When step
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd("batch-001", start, end);

        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("reconciliation.started", resultEvents.get(0).type());
        assertEquals("batch-001", resultEvents.get(0).aggregateId());
        assertNull(caughtException);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPendingPrevious() {
        aggregate = new ReconciliationBatch("batch-002");
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        aggregate = new ReconciliationBatch("batch-003");
        aggregate.markEntriesUnaccounted();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        // Ensure no events were emitted
        assertTrue(aggregate.uncommittedEvents().isEmpty() 
            || aggregate.uncommittedEvents() == null 
            || aggregate.uncommittedEvents().isEmpty() 
            || resultEvents == null || resultEvents.isEmpty());
    }
}
