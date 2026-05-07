package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd command;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Context setup handled in the When block via builder pattern or direct defaults
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Context setup handled in the When block
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    // Default valid command if not overridden by specific Given violations
    if (command == null) {
      command = new StartSessionCmd(
          "session-123",
          "teller-1",
          "terminal-1",
          Instant.now(), // Valid Auth
          "MAIN_MENU"    // Valid Nav State
      );
    }

    try {
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session-123", event.aggregateId());
    assertEquals("teller-1", event.tellerId());
    assertEquals("terminal-1", event.terminalId());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_auth() {
    aggregate = new TellerSessionAggregate("session-123");
    // Violation: AuthenticatedAt is null
    command = new StartSessionCmd("session-123", "teller-1", "terminal-1", null, "MAIN_MENU");
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-123");
    // Violation: AuthenticatedAt is too far in the past
    Instant pastTime = Instant.now().minusSeconds(60); // 60 seconds ago, > 30s limit
    command = new StartSessionCmd("session-123", "teller-1", "terminal-1", pastTime, "MAIN_MENU");
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-123");
    // Violation: NavigationState is null or blank
    command = new StartSessionCmd("session-123", "teller-1", "terminal-1", Instant.now(), "");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // Checking for specific domain exceptions (IllegalStateException or IllegalArgumentException)
    assertTrue(
        capturedException instanceof IllegalStateException || 
        capturedException instanceof IllegalArgumentException,
        "Expected domain error exception but got: " + capturedException.getClass().getSimpleName()
    );
  }
}
