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

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private Exception capturedException;
  private List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    String sessionId = "session-123";
    aggregate = new TellerSessionAggregate(sessionId);
    // Hydrate to a valid active state
    aggregate.markActive("teller-01", "MAIN_MENU");
  }

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // Session ID is handled in the setup of the aggregate in the previous step
    assertNotNull(aggregate.id());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    String sessionId = "session-auth-violation";
    aggregate = new TellerSessionAggregate(sessionId);
    // Mark active but NOT authenticated
    aggregate.markActive("teller-01", "MAIN_MENU");
    aggregate.setUnauthenticated(); // Override to create violation
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    String sessionId = "session-timeout-violation";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markActive("teller-01", "MAIN_MENU");
    aggregate.setInactivityTimeoutExceeded();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    String sessionId = "session-nav-violation";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markActive("teller-01", "MAIN_MENU");
    aggregate.setInvalidNavigationContext();
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
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
    assertFalse(resultEvents.isEmpty());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    assertEquals("session.ended", resultEvents.get(0).type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }
}