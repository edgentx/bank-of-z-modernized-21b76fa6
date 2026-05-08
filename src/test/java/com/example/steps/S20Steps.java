package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {
  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Ensure valid preconditions
    aggregate.setLastActivity(Instant.now());
    aggregate.setNavigationState("HOME");
  }

  @Given("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // sessionId is encapsulated in the aggregate creation
  }

  @When("the EndSessionCmd command is executed")
  public void the_EndSessionCmd_command_is_executed() {
    try {
      Command cmd = new EndSessionCmd(aggregate.id());
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    assertEquals("session.ended", resultEvents.get(0).type());
    assertFalse(aggregate.isActive());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }

  // Scenarios for specific invariants

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-401");
    aggregate.markUnauthenticated();
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-408");
    aggregate.markAuthenticated();
    // Set last activity to 16 minutes ago (timeout is 15)
    aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(16)));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-500");
    aggregate.markAuthenticated();
    aggregate.setNavigationState(null); // Invalid state
  }
}
