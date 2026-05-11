package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionAggregate.TestStateProfile;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    // Valid state: authenticated, active, valid nav
    this.aggregate = TellerSessionAggregate.forTest("session-123", TestStateProfile.VALID);
    this.capturedException = null;
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    this.aggregate = TellerSessionAggregate.forTest("session-123", TestStateProfile.UNAUTHENTICATED);
    this.capturedException = null;
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    this.aggregate = TellerSessionAggregate.forTest("session-123", TestStateProfile.TIMED_OUT);
    this.capturedException = null;
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_nav_state() {
    this.aggregate = TellerSessionAggregate.forTest("session-123", TestStateProfile.INVALID_NAV_STATE);
    this.capturedException = null;
  }

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // sessionId is implicitly handled by the aggregate ID in this domain model context,
    // but we ensure the command matches.
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    Command cmd = new EndSessionCmd(aggregate.id());
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNotNull(resultEvents, "Events list should not be null");
    assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
    
    DomainEvent event = resultEvents.get(0);
    assertTrue(event instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    assertEquals("session.ended", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    
    // Verify aggregate state mutation (sensitive data cleared)
    // Note: We can't easily check internal boolean fields without getters, 
    // but the successful emission implies the transition occurred.
    assertNull(capturedException, "Should not have thrown an exception");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Expected an exception to be thrown");
    assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    
    String message = capturedException.getMessage();
    assertNotNull(message, "Exception message should not be null");
    
    // Ensure no events were emitted
    assertTrue(resultEvents == null || resultEvents.isEmpty(), "No events should be emitted on failure");
  }
}
