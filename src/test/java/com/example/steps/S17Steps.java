package com.example.steps;

import com.example.domain.reconciliation.model.ForceBalanceCmd;
import com.example.domain.reconciliation.model.ReconciliationBalancedEvent;
import com.example.domain.reconciliation.model.ReconciliationBatch;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S17Steps {

    private ReconciliationBatch aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.setPreviousBatchPending(false);
        aggregate.setEntriesAccountedFor(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPendingPrevious() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.setPreviousBatchPending(true);
        aggregate.setEntriesAccountedFor(true);
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedEntries() {
        aggregate = new ReconciliationBatch("batch-123");
        aggregate.setPreviousBatchPending(false);
        aggregate.setEntriesAccountedFor(false);
    }

    @And("a valid batchId is provided")
    public void aValidBatchIdIsProvided() {
        // Implicitly handled by the constructor in the Given steps
    }

    @And("a valid operatorId is provided")
    public void aValidOperatorIdIsProvided() {
        // Implicitly handled in the When step
    }

    @And("a valid justification is provided")
    public void aValidJustificationIsProvided() {
        // Implicitly handled in the When step
    }

    @When("the ForceBalanceCmd command is executed")
    public void theForceBalanceCmdCommandIsExecuted() {
        try {
            ForceBalanceCmd cmd = new ForceBalanceCmd("batch-123", "operator-1", "Manual adjustment approved by supervisor");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a reconciliation.balanced event is emitted")
    public void aReconciliationBalancedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof ReconciliationBalancedEvent, "Event should be ReconciliationBalancedEvent");
        assertEquals("reconciliation.balanced", event.type());
        assertTrue(aggregate.isBalanced(), "Aggregate state should be balanced");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException");
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank(), "Exception should have a message");
    }
}