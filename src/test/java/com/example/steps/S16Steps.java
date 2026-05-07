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

    private ReconciliationBatch aggregate;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;
    private Instant validWindowStart;
    private Instant validWindowEnd;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        // Default state is valid for starting (no pending, entries accounted)
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        validWindowStart = Instant.now().minusSeconds(3600);
        validWindowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousBatchPending() {
        aggregate = new ReconciliationBatch("batch-pending-fail");
        aggregate.markPreviousBatchPending(true);
        // Ensure other invariants are valid to isolate this failure
        validWindowStart = Instant.now().minusSeconds(3600);
        validWindowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        aggregate = new ReconciliationBatch("batch-entries-fail");
        aggregate.markEntriesUnaccounted();
        // Ensure other invariants are valid
        validWindowStart = Instant.now().minusSeconds(3600);
        validWindowEnd = Instant.now();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        StartReconciliationCmd cmd = new StartReconciliationCmd(
                aggregate.id(),
                validWindowStart,
                validWindowEnd
        );
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Should emit exactly one event");
        assertEquals("reconciliation.started", resultingEvents.get(0).type(), "Event type should be reconciliation.started");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "An exception should have been thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Exception should be an IllegalStateException (Domain Error)");
    }
}
