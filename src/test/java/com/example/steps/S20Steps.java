package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private Exception caughtException;
  private String sessionId = "session-123";

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-001");
    aggregate.updateNavigationState("HOME");
  }

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // sessionId is already initialized
    assertNotNull(sessionId);
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    try {
      aggregate.execute(new EndSessionCmd(sessionId));
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNull(caughtException, "Should not have thrown an exception");
    var events = aggregate.uncommittedEvents();
    assertFalse(events.isEmpty(), "Should have emitted an event");
    DomainEvent event = events.get(0);
    assertEquals("session.ended", event.type());
    assertTrue(event instanceof SessionEndedEvent);
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(sessionId);
    // Intentionally not calling markAuthenticated (authenticated = false)
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-001");
    aggregate.updateNavigationState("HOME");
    // Set last activity to 20 minutes ago to simulate timeout
    aggregate.setLastActivityAt(java.time.Instant.now().minus(java.time.Duration.ofMinutes(20)));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-001");
    // Intentionally leaving navigation state null or blank
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException, "Should have thrown an exception");
    assertTrue(caughtException instanceof IllegalStateException);
  }
}
