package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    sessionId = "sess-123";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(); // Setup valid state
  }

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    assertNotNull(sessionId);
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    try {
      resultEvents = aggregate.execute(new EndSessionCmd(sessionId));
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNull(caughtException, "Should not have thrown exception");
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
    assertEquals(sessionId, event.aggregateId());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    sessionId = "sess-invalid-auth";
    aggregate = new TellerSessionAggregate(sessionId);
    // Intentionally NOT calling markAuthenticated() to violate the invariant
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    sessionId = "sess-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.setTimeout(Duration.ofMinutes(30));
    aggregate.simulateInactivity(Duration.ofMinutes(31)); // Violate timeout
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    sessionId = "sess-nav-locked";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.lockNavigation(); // Violate navigation state
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException, "Expected exception but command succeeded");
    // The invariant checks throw IllegalStateException, which is a domain error in this context
    assertTrue(caughtException instanceof IllegalStateException);
  }
}