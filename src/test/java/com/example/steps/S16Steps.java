package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
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

    private ReconciliationBatchAggregate aggregate;
    private final InMemoryReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private StartReconciliationCmd cmd;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        aggregate = new ReconciliationBatchAggregate("batch-123");
        aggregate.markValidReadyState(); // Ensure valid state (IDLE, accounted, no pending)
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now();
        this.cmd = new StartReconciliationCmd("batch-123", start, end);
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("batch-123", event.aggregateId());
        assertEquals("reconciliation.started", event.type());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_batch_pending() {
        aggregate = new ReconciliationBatchAggregate("batch-pending-fail");
        aggregate.markPreviousBatchPending();
        // Setup valid command for this attempt
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now();
        this.cmd = new StartReconciliationCmd("batch-pending-fail", start, end);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_transactions_accounted() {
        aggregate = new ReconciliationBatchAggregate("batch-unaccounted-fail");
        aggregate.markTransactionsUnaccounted();
        // Setup valid command for this attempt
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now();
        this.cmd = new StartReconciliationCmd("batch-unaccounted-fail", start, end);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }
}
