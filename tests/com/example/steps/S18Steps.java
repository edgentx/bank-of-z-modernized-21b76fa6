package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd cmd;
  private Exception thrownException;
  private List<DomainEvent> resultingEvents;

  // Scenario 1: Success
  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
  }

  @And("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Prepared in 'When' step
  }

  @And("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Prepared in 'When' step
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    cmd = new StartSessionCmd("teller-1", "terminal-1");
    try {
      resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNull(thrownException, "Should not throw exception");
    assertNotNull(resultingEvents, "Events should not be null");
    assertEquals(1, resultingEvents.size(), "Should emit one event");
    assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event type mismatch");
    
    SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
    assertEquals("session-123", event.aggregateId());
    assertEquals("teller-1", event.tellerId());
    assertEquals("terminal-1", event.terminalId());
  }

  // Scenario 2: Auth Invariant
  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-auth-fail");
    aggregate.setAuthenticated(false);
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException, "Should throw exception");
    assertTrue(thrownException instanceof IllegalStateException);
  }

  // Scenario 3: Timeout Invariant
  // Shared across S-18/S-19/S-20 feature files. S-18/S-19 reject when the
  // session is in a timed-out state; S-20 rejects when the inactivity-timeout
  // rule itself is being violated (since a timed-out session can still be
  // formally ended). Set both flags so every consumer's rejection scenario
  // fires consistently from the same Gherkin Given.
  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-timeout-fail");
    aggregate.setTimedOut(true);
    aggregate.setInactivityTimeoutRuleViolated(true);
  }

  // Scenario 4: Navigation Invariant
  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation() {
    aggregate = new TellerSessionAggregate("session-nav-fail");
    aggregate.setNavigationStateValid(false);
  }
}