package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private Exception thrownException;
  private java.util.List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    String sessionId = "sess-123";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.updateScreen("MAIN_MENU");
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // sessionId implicitly handled by aggregate construction
  }

  @When("the EndSessionCmd command is executed")
  public void the_EndSessionCmd_command_is_executed() {
    try {
      EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    assertEquals("session.ended", resultEvents.get(0).type());
    assertFalse(aggregate.isActive());
  }

  // --- Failure Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_auth() {
    // Create aggregate but do NOT mark as authenticated
    aggregate = new TellerSessionAggregate("sess-unauth");
    aggregate.updateScreen("LOGIN"); // Valid screen, but not auth
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("sess-timeout");
    aggregate.markAuthenticated();
    // Set last activity to 20 minutes ago
    aggregate.setLastActivity(java.time.Instant.now().minus(java.time.Duration.ofMinutes(20)));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation() {
    aggregate = new TellerSessionAggregate("sess-nav-error");
    aggregate.markAuthenticated();
    // Do not set screen ID, leaving it null/blank
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
  }
}
