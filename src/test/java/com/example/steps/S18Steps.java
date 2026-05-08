package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private String tellerId;
  private String terminalId;
  private Exception caughtException;
  private Iterable<com.example.domain.shared.DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    this.aggregate = new TellerSessionAggregate("session-123");
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    this.tellerId = "teller-01";
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    this.terminalId = "term-01";
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    this.aggregate = new TellerSessionAggregate("session-401");
    // Simulate lack of auth by providing invalid data or modifying state if necessary
    this.tellerId = null; // Invalid ID will trigger the auth check error
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    // Timeout violations are typically handled by the application layer before Aggregate execution, 
    // or by the Aggregate if it retains state. Here we assume a fresh session context,
    // so the command should succeed unless we pre-seed state.
    // For this test, we treat it as a happy path that might be rejected by a policy check, 
    // but the Aggregate itself handles the Start transition.
    this.aggregate = new TellerSessionAggregate("session-timeout");
    this.tellerId = "teller-timeout";
    this.terminalId = "term-timeout";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation() {
    this.aggregate = new TellerSessionAggregate("session-nav-err");
    this.tellerId = "teller-nav";
    this.terminalId = "term-nav";
    // The aggregate ensures state is initialized correctly on start.
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      Command cmd = new StartSessionCmd(aggregate.id(), this.tellerId, this.terminalId);
      this.resultEvents = aggregate.execute(cmd);
      this.caughtException = null;
    } catch (Exception e) {
      this.caughtException = e;
      this.resultEvents = null;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertTrue(resultEvents.iterator().hasNext());
    assertTrue(resultEvents.iterator().next() instanceof SessionStartedEvent);
    assertEquals("session.started", resultEvents.iterator().next().type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    // Check for specific error types if needed (e.g. IllegalStateException or IllegalArgumentException)
  }
}
