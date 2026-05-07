package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated();
    aggregate.setLastActivity(Instant.now());
    aggregate.setCurrentContext("MAIN_MENU");
  }

  @Given("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Session ID is implicitly handled by the aggregate instance creation
  }

  @When("the EndSessionCmd command is executed")
  public void the_EndSessionCmd_command_is_executed() {
    try {
      Command cmd = new EndSessionCmd("session-123");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
    assertEquals("session-123", event.aggregateId());
    assertEquals("session.ended", event.type());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-401");
    aggregate.markUnauthenticated(); // Violates invariant
    aggregate.setLastActivity(Instant.now());
    aggregate.setCurrentContext("MAIN_MENU");
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-408");
    aggregate.markAuthenticated();
    // Set activity to 20 minutes ago (Timeout is 15)
    aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    aggregate.setCurrentContext("MAIN_MENU");
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-nav-error");
    aggregate.markAuthenticated();
    aggregate.setLastActivity(Instant.now());
    aggregate.invalidateNavigationState(); // Sets context to null, violating invariant
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
    assertNull(resultEvents);
  }
}
