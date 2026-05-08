package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermgmt.model.SessionStartedEvent;
import com.example.domain.tellermgmt.model.StartSessionCmd;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;
  private String tellerId;
  private String terminalId;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    this.aggregate = new TellerSessionAggregate("session-123");
    this.aggregate.markAuthenticated(); // Ensure valid state by default
    this.aggregate.setNavigationState("HOME");
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    this.tellerId = "teller-001";
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    this.terminalId = "term-A";
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    this.aggregate = new TellerSessionAggregate("session-auth-fail");
    this.aggregate.markUnauthenticated(); // Trigger auth violation
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    this.aggregate = new TellerSessionAggregate("session-timeout");
    this.aggregate.markAuthenticated(); // Auth is fine
    this.aggregate.markTimedOut(); // Trigger timeout violation
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    this.aggregate = new TellerSessionAggregate("session-nav-fail");
    this.aggregate.markAuthenticated(); // Auth is fine
    this.aggregate.setNavigationState("INVALID"); // Trigger nav state violation
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
      this.resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      this.capturedException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session.started", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertEquals(tellerId, event.tellerId());
    assertEquals(terminalId, event.terminalId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // Depending on the specific invariant violated, the exception type might vary (IllegalStateException, IllegalArgumentException)
    // Here we just check that an exception was thrown indicating rejection.
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
  }
}
