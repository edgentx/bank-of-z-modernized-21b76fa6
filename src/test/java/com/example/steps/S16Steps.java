package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    // Helper to reset state
    private void createNewAggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.thrownException = null;
        this.resultingEvents = null;
    }

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        createNewAggregate();
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // Implicitly used in the 'When' step via the Command
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_pending() {
        createNewAggregate();
        aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        createNewAggregate();
        aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now();
        StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), start, end);

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Expected exactly one event");
        assertTrue(resultingEvents.get(0) instanceof ReconciliationStartedEvent, "Expected ReconciliationStartedEvent");
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultingEvents.get(0);
        assertEquals("batch-123", event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}