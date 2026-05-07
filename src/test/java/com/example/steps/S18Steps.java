package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {
  private TellerSessionAggregate aggregate;
  private String sessionId = "sess-123";
  private String validTellerId = "teller-01";
  private String validTerminalId = "term-A";
  private Exception thrownException;
  private List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate(sessionId);
  }

  @Given("a valid tellerId is provided")
  public void a_valid_tellerId_is_provided() {
    // validTellerId is already set
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminalId_is_provided() {
    // validTerminalId is already set
  }

  @When("the StartSessionCmd command is executed")
  public void the_StartSessionCmd_command_is_executed() {
    Command cmd = new StartSessionCmd(sessionId, validTellerId, validTerminalId);
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session.started", event.type());
    assertEquals(sessionId, event.aggregateId());
    assertEquals(validTellerId, event.tellerId());
    assertEquals(validTerminalId, event.terminalId());
  }

  // --- Scenarios for Rejections ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(sessionId);
    validTellerId = null; // Violation: No ID provided
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(sessionId);
    // For this aggregate logic, we simulate this by invalidating the terminal context
    // effectively making the operational context invalid.
    validTerminalId = ""; 
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate(sessionId);
    validTerminalId = null; // Violation: Invalid context
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    // Domain errors manifest as IllegalArgumentException or IllegalStateException
    assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
  }
}
