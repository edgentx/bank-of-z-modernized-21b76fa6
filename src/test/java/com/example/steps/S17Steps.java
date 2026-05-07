package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryReconciliationBatchRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S17Steps {

    private final ReconciliationBatchRepository repository = new InMemoryReconciliationBatchRepository();
    private ReconciliationBatch aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void a_valid_ReconciliationBatch_aggregate() {
        String batchId = "batch-" + UUID.randomUUID();
        this.aggregate = new ReconciliationBatch(batchId);
        // Initialize to a valid open state
        aggregate.apply(new ReconciliationStartedEvent(batchId, "op-1", Instant.now()));
        repository.save(aggregate);
    }

    @Given("a valid batchId is provided")
    public void a_valid_batchId_is_provided() {
        // Handled by the aggregate initialization
        assertNotNull(aggregate.id());
    }

    @Given("a valid operatorId is provided")
    public void a_valid_operatorId_is_provided() {
        // Will be used in command construction
    }

    @Given("a valid justification is provided")
    public void a_valid_justification_is_provided() {
        // Will be used in command construction
    }

    @When("the ForceBalanceCmd command is executed")
    public void the_ForceBalanceCmd_command_is_executed() {
        try {
            ForceBalanceCmd cmd = new ForceBalanceCmd(aggregate.id(), "op-123", "Manual override authorization");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.balanced event is emitted")
    public void a_reconciliation_balanced_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationBalancedEvent);
        assertEquals("reconciliation.balanced", resultEvents.get(0).type());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void a_ReconciliationBatch_aggregate_that_violates_pending_constraint() {
        String batchId = "batch-blocked-pending";
        this.aggregate = new ReconciliationBatch(batchId);
        // Force the aggregate into a state where it thinks a previous batch is pending
        // In a real scenario, this might involve checking a shared state, 
        // here we simulate the aggregate's internal validation logic trigger.
        aggregate.apply(new ReconciliationStartedEvent(batchId, "op-1", Instant.now()));
        // We'll simulate the rejection condition via the command validation logic inside the aggregate.
        // For this test, we pass a flag or context to the mock if needed, 
        // but cleaner is to have the aggregate check a state variable.
        // Let's assume the aggregate checks 'isPreviousPending'.
        aggregate.simulatePendingConstraint(true); 
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void a_ReconciliationBatch_aggregate_that_violates_accounting_constraint() {
        String batchId = "batch-blocked-missing";
        this.aggregate = new ReconciliationBatch(batchId);
        aggregate.apply(new ReconciliationStartedEvent(batchId, "op-1", Instant.now()));
        // Simulate missing entries state
        aggregate.simulateMissingEntries(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}