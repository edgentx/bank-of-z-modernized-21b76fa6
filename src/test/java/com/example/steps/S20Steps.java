package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    this.sessionId = "session-123";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(); // Ensure it is valid/authenticated for happy path
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // Session ID is initialized in the previous step
    assertNotNull(sessionId);
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    this.sessionId = "session-auth-violation";
    this.aggregate = new TellerSessionAggregate(sessionId);
    // Intentionally do NOT markAuthenticated()
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    this.sessionId = "session-timeout-violation";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Set last activity to 31 minutes ago (configured timeout is 30 mins)
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation() {
    this.sessionId = "session-nav-violation";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.invalidateNavigationState();
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    try {
      EndSessionCmd cmd = new EndSessionCmd(sessionId);
      this.resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      this.caughtException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNull(caughtException, "Should not have thrown exception: " + caughtException);
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
    assertEquals("session.ended", event.type());
    assertEquals(sessionId, event.aggregateId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException, "Expected exception but command succeeded");
    assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException, got " + caughtException.getClass().getSimpleName());
    // Validate the message matches one of the invariant violations based on the scenario context
    String message = caughtException.getMessage();
    assertTrue(
        message.contains("authenticated") || 
        message.contains("timeout") || 
        message.contains("Navigation state"),
        "Unexpected error message: " + message
    );
  }
}
