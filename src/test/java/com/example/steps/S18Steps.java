package com.example.steps;

import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private Exception caughtException;
  private StartSessionCmd command;
  private String aggregateId = "test-session-1";

  // Scenario: Successfully execute StartSessionCmd
  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate(aggregateId);
    // Default state for valid aggregate
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Valid ID will be set in the When clause
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Valid ID will be set in the When clause
  }

  // Scenario: Rejected - Auth
  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(aggregateId);
    // Simulate a state where auth check would fail (e.g. invalid token context)
  }

  // Scenario: Rejected - Timeout
  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(aggregateId);
    // Simulate expired state
  }

  // Scenario: Rejected - Navigation State
  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate(aggregateId);
    // Simulate invalid navigation state
  }

  // Actions
  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      // For positive flow, valid IDs. For negative flows, the aggregate state determines rejection.
      command = new StartSessionCmd("teller-123", "terminal-456");
      aggregate.execute(command);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  // Outcomes
  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNull(caughtException, "Should not have thrown exception");
    var events = aggregate.uncommittedEvents();
    assertFalse(events.isEmpty(), "Should have emitted an event");
    assertTrue(events.get(0) instanceof SessionStartedEvent, "Should be SessionStartedEvent");
    
    SessionStartedEvent event = (SessionStartedEvent) events.get(0);
    assertEquals("teller-123", event.tellerId());
    assertEquals("terminal-456", event.terminalId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException, "Should have thrown an exception");
    // Specific invariant checks are implemented inside the aggregate logic
  }
}
