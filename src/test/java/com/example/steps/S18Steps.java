package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S18Steps {
  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    String id = "session-123";
    aggregate = new TellerSessionAggregate(id);
    // Assume valid setup implies authenticated for success case unless specified otherwise
    aggregate.markAuthenticated(); 
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Value handled in When step construction
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Value handled in When step construction
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-A");
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNull(capturedException, "Should not have thrown exception: " + capturedException);
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session.started", event.type());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-123");
    // Deliberately not calling markAuthenticated()
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // In this domain logic, we throw IllegalStateException for invariants.
    // Typically in DDD, this might be a custom DomainException, but we follow the existing pattern of RuntimeExceptions in the prompt.
    assertTrue(capturedException instanceof IllegalStateException);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated();
    // Set last activity to > 30 minutes ago (Current implementation check is relative to 'now' if lastActivityAt is set)
    aggregate.setLastActivityAt(Instant.now().minusSeconds(1801)); // 30 mins 1 sec
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_context() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated();
    // Set the specific invalid context string that the Aggregate logic checks against
    aggregate.setOperationalContext("INVALID_CONTEXT");
  }
}