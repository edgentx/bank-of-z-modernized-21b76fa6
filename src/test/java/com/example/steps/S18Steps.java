package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate("sess-123");
    aggregate.markAuthenticated(); // Satisfy auth invariant by default for valid agg
  }

  @And("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Handled in When construction
  }

  @And("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Handled in When construction
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      cmd = new StartSessionCmd("sess-123", "teller-1", "term-42");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertEquals("session.started", resultEvents.get(0).type());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_auth() {
    aggregate = new TellerSessionAggregate("sess-bad-auth");
    // isAuthenticated defaults to false
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("sess-timeout");
    aggregate.markAuthenticated();
    aggregate.markInactive(); // Sets lastActivityAt far in the past
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("sess-bad-state");
    aggregate.markAuthenticated();
    aggregate.corruptState(); // Sets state to UNKNOWN
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
  }
}
