package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> events;
    private String batchId = "batch-123";
    private String batchWindow = "2023-10-01";

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        aggregate = new ReconciliationBatch(batchId);
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // batchWindow is defaulted in the field, effectively "provided" for the scenario
        assertNotNull(batchWindow);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_previous_pending() {
        aggregate = new ReconciliationBatch(batchId);
        aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_entries_accounted() {
        aggregate = new ReconciliationBatch(batchId);
        aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        // Reload from repository to simulate standard flow
        aggregate = repository.findById(batchId).orElseThrow();
        StartReconciliationCmd cmd = new StartReconciliationCmd(batchId, batchWindow);

        try {
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) events.get(0);
        assertEquals(batchId, event.aggregateId());
        assertEquals("ReconciliationStarted", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
