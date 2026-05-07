package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S16Steps {

    private ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_pending() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // Context setup, usually implicit in the command construction in the 'When' step
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            Instant start = Instant.now().minusSeconds(3600);
            Instant end = Instant.now();
            StartReconciliationCmd cmd = new StartReconciliationCmd("batch-123", start, end);
            
            // Reload to ensure clean state from repo
            var agg = repository.findById("batch-123").orElseThrow();
            resultEvents = agg.execute(cmd);
            
            // Save updated state
            repository.save(agg);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("reconciliation.started", event.type());
        Assertions.assertEquals("batch-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // In this domain implementation, errors are modeled as IllegalStateException (a form of domain error)
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
        Assertions.assertTrue(caughtException.getMessage().contains("Cannot execute batch"));
    }
}
