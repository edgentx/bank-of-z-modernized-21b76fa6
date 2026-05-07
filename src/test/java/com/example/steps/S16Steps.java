package com.example.steps;

import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
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

  private ReconciliationBatchAggregate aggregate;
  private Instant windowStart;
  private Instant windowEnd;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid ReconciliationBatch aggregate")
  public void a_valid_ReconciliationBatch_aggregate() {
    String batchId = "BATCH-" + System.currentTimeMillis();
    aggregate = new ReconciliationBatchAggregate(batchId);
  }

  @Given("a valid batchWindow is provided")
  public void a_valid_batchWindow_is_provided() {
    windowStart = Instant.now().minusSeconds(3600);
    windowEnd = Instant.now();
  }

  @Given("a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.")
  public void a_ReconciliationBatch_aggregate_that_violates_pending_batch() {
    // Setup a valid batch
    String batchId = "BATCH-PENDING-" + System.currentTimeMillis();
    aggregate = new ReconciliationBatchAggregate(batchId);
    
    // Simulate a pending state by running a valid command first
    Instant start = Instant.now().minusSeconds(7200);
    Instant end = Instant.now().minusSeconds(3600);
    StartReconciliationCmd initCmd = new StartReconciliationCmd(batchId, start, end);
    aggregate.execute(initCmd);
    
    // Setup the new window for the next attempt
    windowStart = Instant.now().minusSeconds(3600);
    windowEnd = Instant.now();
  }

  @Given("a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.")
  public void a_ReconciliationBatch_aggregate_that_violates_accounting() {
    String batchId = "BATCH-INVALID-DATA-" + System.currentTimeMillis();
    aggregate = new ReconciliationBatchAggregate(batchId);
    
    // We simulate invalid data by providing an invalid window (start == end)
    // which the aggregate interprets as a data integrity failure (empty period to reconcile)
    windowStart = Instant.now();
    windowEnd = Instant.now(); // Invalid: zero duration
  }

  @When("the StartReconciliationCmd command is executed")
  public void the_StartReconciliationCmd_command_is_executed() {
    try {
      StartReconciliationCmd cmd = new StartReconciliationCmd(aggregate.id(), windowStart, windowEnd);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a reconciliation.started event is emitted")
  public void a_reconciliation_started_event_is_emitted() {
    assertNotNull(resultEvents, "Events should not be null");
    assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
    
    DomainEvent event = resultEvents.get(0);
    assertTrue(event instanceof ReconciliationStartedEvent, "Event must be ReconciliationStartedEvent");
    
    ReconciliationStartedEvent startedEvent = (ReconciliationStartedEvent) event;
    assertEquals("reconciliation.started", startedEvent.type());
    assertEquals(aggregate.id(), startedEvent.aggregateId());
    assertEquals(windowStart, startedEvent.windowStart());
    assertEquals(windowEnd, startedEvent.windowEnd());
    
    // Verify aggregate state mutation
    assertEquals(ReconciliationBatchAggregate.Status.STARTED, aggregate.getStatus());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException, "An exception should have been thrown");
    assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
        "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
  }
}