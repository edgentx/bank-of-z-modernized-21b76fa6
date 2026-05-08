package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermgmt.model.SessionStartedEvent;
import com.example.domain.tellermgmt.model.StartSessionCmd;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  // Scenario 1: Success
  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate("session-1");
  }

  @And("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Placeholder for command construction context
  }

  @And("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    cmd = new StartSessionCmd("session-1", "teller-100", "terminal-200");
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent evt = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session-1", evt.aggregateId());
    assertEquals("teller-100", evt.tellerId());
    assertEquals("terminal-200", evt.terminalId());
  }

  // Scenario 2: Auth Error
  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_auth() {
    aggregate = new TellerSessionAggregate("session-2");
    aggregate.markUnauthenticated();
    cmd = new StartSessionCmd("session-2", "teller-100", "terminal-200");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
    assertTrue(caughtException.getMessage().contains("authenticated"));
  }

  // Scenario 3: Timeout Error
  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-3");
    aggregate.markTimedOut();
    cmd = new StartSessionCmd("session-3", "teller-100", "terminal-200");
  }

  // Scenario 4: Navigation Error
  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation() {
    aggregate = new TellerSessionAggregate("session-4");
    aggregate.markInvalidNavigation();
    cmd = new StartSessionCmd("session-4", "teller-100", "terminal-200");
  }
}
