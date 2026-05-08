package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;
  private static final String SESSION_ID = "session-123";

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    // Ensure it meets the "authenticated" and "active" invariants by default
    aggregate.markAuthenticated(); 
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // sessionId is constant in this test context
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    // Do NOT mark authenticated. Defaults to false.
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    aggregate.markAuthenticated(); // Authenticated
    aggregate.markInactive();       // But timed out
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    aggregate.markAuthenticated();
    aggregate.invalidateNavigationState(); // Breaks the invariant
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    try {
      EndSessionCmd cmd = new EndSessionCmd(SESSION_ID);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
    
    TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
    assertEquals("session.ended", event.type());
    assertEquals(SESSION_ID, event.aggregateId());
    assertTrue(aggregate.isEnded());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // In this domain, we use IllegalStateException to represent invariant violations
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
