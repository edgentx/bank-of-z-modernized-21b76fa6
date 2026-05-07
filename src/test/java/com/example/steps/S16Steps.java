package com.example.steps;

import com.example.domain.reconciliation.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S16Steps {

    private ReconciliationBatch aggregate;
    private Instant batchWindowStart;
    private Instant batchWindowEnd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid ReconciliationBatch aggregate")
    public void aValidReconciliationBatchAggregate() {
        aggregate = new ReconciliationBatch("batch-123");
    }

    @And("a valid batchWindow is provided")
    public void aValidBatchWindowIsProvided() {
        batchWindowStart = Instant.now().minusSeconds(3600);
        batchWindowEnd = Instant.now();
    }

    @When("the StartReconciliationCmd command is executed")
    public void theStartReconciliationCmdCommandIsExecuted() {
        try {
            var cmd = new StartReconciliationCmd(aggregate.id(), batchWindowStart, batchWindowEnd);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a reconciliation.started event is emitted")
    public void aReconciliationStartedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof ReconciliationStartedEvent);
        
        var event = (ReconciliationStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("reconciliation.started", event.type());
        Assertions.assertEquals("batch-123", event.aggregateId());
    }

    @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
    public void aReconciliationBatchAggregateThatViolatesPreviousBatchPending() {
        aggregate = new ReconciliationBatch("batch-pending-123");
        aggregate.markPreviousBatchPending(true);
        batchWindowStart = Instant.now().minusSeconds(3600);
        batchWindowEnd = Instant.now();
    }

    @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
    public void aReconciliationBatchAggregateThatViolatesEntriesAccounted() {
        aggregate = new ReconciliationBatch("batch-unaccounted-123");
        aggregate.markEntriesUnaccounted();
        batchWindowStart = Instant.now().minusSeconds(3600);
        batchWindowEnd = Instant.now();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // We check for IllegalStateException or IllegalArgumentException as domain error representation
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
