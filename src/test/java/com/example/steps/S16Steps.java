package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.domain.shared.Command;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch batch;
    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private Exception caughtException;
    private List resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        batch = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // Context setup handled in the When step
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_pending() {
        batch = new ReconciliationBatch("batch-pending");
        batch.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_accounting() {
        batch = new ReconciliationBatch("batch-unaccounted");
        batch.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            Command cmd = new StartReconciliationCmd(batch.id(), "2023-10-01", "2023-10-31");
            resultEvents = batch.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent, "Event should be ReconciliationStartedEvent");
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("batch-123", event.aggregateId());
        assertEquals("reconciliation.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (domain error)");
    }
}
