package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.reconciliation.model.ReconciliationStartedEvent;
import com.example.domain.reconciliation.model.StartReconciliationCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private StartReconciliationCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_reconciliation_batch_aggregate() {
        this.aggregate = new ReconciliationBatch("batch-123");
    }

    @Given("a valid batchWindow is provided")
    public void a_valid_batch_window_is_provided() {
        // Command construction happens in the When step, 
        // this just ensures we are ready to use valid defaults.
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_reconciliation_batch_aggregate_that_violates_previous_pending() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markPreviousBatchPending(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_reconciliation_batch_aggregate_that_violates_entries_accounted() {
        this.aggregate = new ReconciliationBatch("batch-123");
        this.aggregate.markEntriesUnaccounted();
    }

    @When("the StartReconciliationCmd command is executed")
    public void the_start_reconciliation_cmd_command_is_executed() {
        Instant now = Instant.now();
        this.cmd = new StartReconciliationCmd(
                aggregate.id(),
                now.minusSeconds(3600),
                now,
                "operator-1"
        );

        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void a_reconciliation_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);

        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(cmd.batchWindowStart(), event.batchWindowStart());
        assertEquals(cmd.batchWindowEnd(), event.batchWindowEnd());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
