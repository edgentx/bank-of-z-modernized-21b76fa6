package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;
  private Instant fixedTimestamp = Instant.now();
  private Duration timeout = Duration.ofMinutes(30);

  // Given Steps

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    // Create a fully valid aggregate
    aggregate = new TellerSessionAggregate(
        "session-123",
        true,  // authenticated
        fixedTimestamp,
        timeout,
        "HOME", // valid context
        false   // not ended
    );
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // The ID is implicitly handled by the aggregate creation above
    // This step documents the state for the scenario
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate(
        "session-401",
        false, // NOT authenticated
        fixedTimestamp,
        timeout,
        "HOME",
        false
    );
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatIsTimedOut() {
    // Set last activity to 2 hours ago (longer than 30 min timeout)
    aggregate = new TellerSessionAggregate(
        "session-timeout",
        true,
        fixedTimestamp.minus(Duration.ofHours(2)),
        timeout,
        "HOME",
        false
    );
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateInCriticalNavigationState() {
    // Set context to a state that prohibits termination
    aggregate = new TellerSessionAggregate(
        "session-nav-error",
        true,
        fixedTimestamp,
        timeout,
        "CRITICAL_TRANSACTION", // Invalid context for ending
        false
    );
  }

  // When Steps

  @When("the EndSessionCmd command is executed")
  public void theEndSessionCmdCommandIsExecuted() {
    try {
      Command cmd = new EndSessionCmd(aggregate.id());
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  // Then Steps

  @Then("a session.ended event is emitted")
  public void aSessionEndedEventIsEmitted() {
    Assertions.assertNotNull(resultEvents, "Events list should not be null");
    Assertions.assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
    Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event must be SessionEndedEvent");
    
    SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
    Assertions.assertEquals("session.ended", event.type());
    Assertions.assertEquals("session-123", event.aggregateId());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
    Assertions.assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
  }
}
