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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private StartReconciliationCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Setup and Teardown for registry state
    public S16Steps() {
        // Ensure clean state before scenarios if needed, though @Before is preferred.
        // Relying on Cucumber lifecycle or explicit manual reset if not using hooks.
    }

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        ReconciliationBatch.clearRegistry();
        String id = UUID.randomUUID().toString();
        this.aggregate = new ReconciliationBatch(id);
    }

    @Given("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.command = new StartReconciliationCmd(
            aggregate.id(),
            start,
            end,
            Set.of("acct-1", "acct-2")
        );
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPendingStatus() {
        // 1. Create a "Previous" batch and mark it as active in the registry
        String previousBatchId = "previous-batch-123";
        ReconciliationBatch.clearRegistry();
        
        // We manually simulate a previous batch existing by adding it to the static registry
        // In a real test, we'd create a previous aggregate and execute a command on it.
        // Here we mock the registry state directly for the invariant check.
        ReconciliationBatch previousBatch = new ReconciliationBatch(previousBatchId);
        // Simulate it being started
        ReconciliationBatch.clearRegistry(); // Reset first
        // Hack: Use a dummy ID to occupy the registry to satisfy the invariant check "!activeBatchIds.isEmpty()"
        // or rely on the check logic implemented. 
        // Let's assume we can't instantiate a fully 'started' batch without command, 
        // so we will manipulate the scenario to create our NEW batch, but pretend the OLD one is there.
        
        this.aggregate = new ReconciliationBatch(UUID.randomUUID().toString());
        
        // Setup valid command data first
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        this.command = new StartReconciliationCmd(
            aggregate.id(),
            start,
            end,
            Set.of("acct-1")
        );

        // Mark a dummy batch as active to trigger the invariant failure
        // Note: This requires exposing a way to set the active state or creating a real previous aggregate.
        // Since ReconciliationBatch adds itself to activeBatchIds on success, we can't easily "fake" it 
        // without reflection or modifying the class. 
        // However, for the sake of the BDD step, we assume the Domain Logic handles the check.
        // I will modify the Domain class `ReconciliationBatch` to accept a flag or simply rely on 
        // the aggregate's own state if the rule implies *this* aggregate.
        // Prompt says: "A reconciliation batch cannot be executed if a previous batch is still pending."
        // This implies a global check. 
        // Let's create the previous batch and start it properly.
        
        ReconciliationBatch previous = new ReconciliationBatch("prev-batch");
        StartReconciliationCmd prevCmd = new StartReconciliationCmd("prev-batch", Instant.now().minusSeconds(7200), Instant.now().minusSeconds(3600), Set.of("acct-old"));
        previous.execute(prevCmd); // This puts "prev-batch" in the registry.
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesMissingAccounts() {
        ReconciliationBatch.clearRegistry();
        String id = UUID.randomUUID().toString();
        this.aggregate = new ReconciliationBatch(id);
        
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        
        // Violation: Empty or null account list
        this.command = new StartReconciliationCmd(
            id, start, end, Set.of() 
        );
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ReconciliationStartedEvent);
        
        ReconciliationStartedEvent event = (ReconciliationStartedEvent) resultEvents.get(0);
        assertEquals("reconciliation.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Domain errors manifest as IllegalStateException or IllegalArgumentException in this model
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}