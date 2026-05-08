package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Setup for success case
  }

  @Given("a valid tellerId is provided")
  public void a_valid_tellerId_is_provided() {
    // Command creation is deferred to When step to utilize context
  }

  @Given("a valid terminalId is provided")
  public void a valid_terminalId_is_provided() {
    // Command creation is deferred to When step to utilize context
  }

  @When("the StartSessionCmd command is executed")
  public void the_StartSessionCmd_command_is_executed() {
    try {
      cmd = new StartSessionCmd("session-123", "teller-1", "terminal-1");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    // 'authenticated' flag defaults to false in constructor
    aggregate = new TellerSessionAggregate("session-unauth");
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    // This invariant is currently assumed valid on start, but we mock the structure
    aggregate = new TellerSessionAggregate("session-timeout");
    aggregate.markAuthenticated();
    // In a real implementation, we might check lastActivityAt against a timeout threshold
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-nav-error");
    aggregate.markAuthenticated();
    aggregate.markActive(); // Simulating inconsistent state (already active)
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    // Checking for standard Java exceptions thrown by business logic (IllegalStateException, IllegalArgumentException)
    assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
  }
}
