package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
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

    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        String id = "batch-123";
        aggregate = new ReconciliationBatch(id);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_with_previous_pending() {
        String id = "batch-pending";
        aggregate = new ReconciliationBatch(id);
        aggregate.markPreviousBatchPending(true);
        repository.save(aggregate);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_with_missing_entries() {
        String id = "batch-missing";
        aggregate = new ReconciliationBatch(id);
        aggregate.markEntriesUnaccounted();
        repository.save(aggregate);
    }

    @And("a valid batchWindow is provided")
    public void a_valid_batchWindow_is_provided() {
        // Context setup, handled in the When step via the command object
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_StartReconciliationCmd_command_is_executed() {
        try {
            Instant start = Instant.now().minusSeconds(3600);
            Instant end = Instant.now();
            StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), start, end);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("ReconciliationStarted", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
