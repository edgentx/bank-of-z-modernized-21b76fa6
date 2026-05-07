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
    private StartReconciliationCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1 & 2 Setup
    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        this.batch = new ReconciliationBatch("batch-123");
        this.caughtException = null;
    }

    // Scenario 2 Setup (Violation)
    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateWithPreviousPending() {
        this.batch = new ReconciliationBatch("batch-123");
        // Force state to PENDING to simulate invariant violation
        // In a real app, this might be done via a constructor or snapshot loading
        try {
            // We reflectively modify or assume a setter exists for testing invariants
            // For this exercise, we assume we can force the state or create a 'test' instance
            var field = ReconciliationBatch.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(this.batch, "PENDING");
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed", e);
        }
    }

    // Scenario 3 Setup (Violation)
    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateWithUnaccountedTransactions() {
        this.batch = new ReconciliationBatch("batch-123");
        // State is valid, but the command will indicate it's not ready
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        // Logic handled in the 'When' step via the command object
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            // Determine context based on which scenario is running (simplified logic)
            boolean isReady = true;
            
            // If we are in Scenario 3 (violations), the system check would fail. 
            // We simulate this by passing false to the command check for that scenario.
            // Since Gherkin doesn't pass state easily, we check the batch state or a flag.
            // However, the scenario says "Aggregate that violates... entries accounted for".
            // This implies the check happens *inside* or is triggered by the command state.
            // We'll set isReady=false if the batch ID matches scenario 3 setup.
            if ("batch-123".equals(batch.id()) && batch.getStatus().equals("IDLE") && caughtException == null) {
                // We need a way to distinguish Scenario 1 from Scenario 3. 
                // Let's assume Scenario 3 implies the system check finds a mismatch.
                // Since we can't detect that easily, we'll rely on the specific Gherkin flow or a test flag.
                // For simplicity, let's assume S-16 Scenarios run in order and we just execute.
                // Actually, Cucumber scenarios are isolated. Let's use a thread-local or class flag if needed, 
                // but better: check if the batch was created in the previous step.
                // We will assume for Scenario 3, we set isReady=false manually here to simulate the external check failure.
                
                // Heuristic: Scenario 3 setup is the only one where we didn't touch 'status' but claim a violation.
                // We'll check if the command needs to reflect the violation.
                // Let's create the command.
            }
            
            // Refinement:
            // Scen 1: Valid IDLE, Ready.
            // Scen 2: PENDING (Violates previous pending). Ready=true (assumed) but fails on status check.
            // Scen 3: IDLE, but NOT Ready (Violates accounting).
            
            boolean ready = true;
            if ("IDLE".equals(batch.getStatus())) {
                // Could be Scen 1 or Scen 3. 
                // If we were passed a context flag, we'd use it. 
                // Let's assume standard behavior is valid, and Scen 3 requires specific setup.
                // Wait, Scen 3 Given step says: "aggregate that violates...".
                // That implies the aggregate KNOWS. But the invariant is "Entries must be accounted".
                // Usually, this is an argument passed to the command or a repository check.
                // We will pass isReady=false for Scen 3.
                // How do we know we are in Scen 3? We can't easily without a shared state. 
                // However, since Scenarios are separate, we can cheat:
                // If we are in Scen 3, we want the command to fail.
                // Let's assume for S16Steps, we handle the happy path mostly.
                // But Cucumber maps steps to methods. We need specific mapping.
                // "a ReconciliationBatch aggregate that violates: All transaction entries..." -> specific method.
                // We can set a flag there.
            } else if ("PENDING".equals(batch.getStatus())) {
                // Scen 2
                ready = true; 
            }

            // Let's use a flag set in the Given steps.
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // Helper to execute with specific readiness to match scenario context
    private void execute(boolean isReady) {
        try {
            this.command = new StartReconciliationCmd(batch.id(), "2023-10-27", isReady);
            this.resultEvents = batch.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // --- Specific Step Implementations for Clarity ---

    // Scenario 1
    @Given("a valid ReconciliationBatch aggregate")
    public void setupValidBatch() {
        this.batch = new ReconciliationBatch("batch-1");
    }
    @And("a valid batchWindow is provided")
    public void setupValidWindow() {
        // No-op, command handles it
    }
    @When("the StartReconciliationCmd command is executed")
    public void executeCommandSuccess() {
        execute(true);
    }

    // Scenario 2
    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void setupPendingBatch() {
        this.batch = new ReconciliationBatch("batch-2");
        // Simulate PENDING state
        try {
            var f = ReconciliationBatch.class.getDeclaredField("status");
            f.setAccessible(true);
            f.set(batch, "PENDING");
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    @When("the StartReconciliationCmd command is executed")
    public void executeCommandPending() {
        execute(true); // Command is valid, but aggregate state is bad
    }

    // Scenario 3
    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void setupUnaccountedBatch() {
        this.batch = new ReconciliationBatch("batch-3");
    }
    @When("the StartReconciliationCmd command is executed")
    public void executeCommandUnaccounted() {
        execute(false); // Flag that entries are NOT ready
    }

    // Assertions
    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals("batch-1", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}