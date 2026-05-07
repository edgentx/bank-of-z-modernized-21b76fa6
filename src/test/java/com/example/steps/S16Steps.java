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
    private StartReconciliationCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        batch = new ReconciliationBatch("batch-123");
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_pending() {
        batch = new ReconciliationBatch("batch-123");
        batch.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        batch = new ReconciliationBatch("batch-123");
        batch.markEntriesUnaccounted();
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // cmd construction happens in the When step for simplicity in this context, 
        // or we store the window parameters here.
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        cmd = new StartReconciliationCmd("batch-123", start, end, "operator-1");

        try {
            resultingEvents = batch.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultingEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
