package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S16Steps {

    private ReconciliationBatch batch;
    private Instant batchWindowStart;
    private Instant batchWindowEnd;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        batch = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        this.batchWindowStart = Instant.now();
        this.batchWindowEnd = this.batchWindowStart.plus(Duration.ofHours(1));
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that violates_previous_pending() {
        batch = new ReconciliationBatch("batch-123");
        batch.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that violates_entries_unaccounted() {
        batch = new ReconciliationBatch("batch-123");
        batch.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            // We assume a default window for tests where it wasn't explicitly set in the Given
            Instant start = (batchWindowStart != null) ? batchWindowStart : Instant.now();
            Instant end = (batchWindowEnd != null) ? batchWindowEnd : start.plus(Duration.ofHours(1));
            
            Command cmd = new StartReconciliationCmd(batch.id(), start, end);
            resultEvents = batch.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("ReconciliationStarted", event.type());
        Assertions.assertEquals("batch-123", event.aggregateId());
        Assertions.assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In this domain, we are using RuntimeExceptions (IllegalStateException/IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
