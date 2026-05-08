package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private String tellerId;
  private String terminalId;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    String sessionId = "TS-123";
    aggregate = new TellerSessionAggregate(sessionId);
    // Set up valid defaults for a successful start
    aggregate.markAuthenticated(true);
    aggregate.setCurrentContext("HOME");
    aggregate.setLastActivityAt(Instant.now()); // Active
  }

  @And("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    this.tellerId = "TELLER-01";
  }

  @And("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    this.terminalId = "TERM-042";
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    String sessionId = "TS-NO-AUTH";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(false); // Violation: Not authenticated
    aggregate.setCurrentContext("HOME");
    this.tellerId = "TELLER-01";
    this.terminalId = "TERM-042";
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    String sessionId = "TS-TIMEOUT";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(true);
    // Simulate a session that has expired
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(1)));
    this.tellerId = "TELLER-01";
    this.terminalId = "TERM-042";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    String sessionId = "TS-NAV-ERR";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(true);
    // Violation: Session is in a transaction state, cannot 'Start' again from here
    aggregate.setCurrentContext("TX_IN_PROGRESS");
    this.tellerId = "TELLER-01";
    this.terminalId = "TERM-042";
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (IllegalStateException e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session.started", event.type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
  }
}
