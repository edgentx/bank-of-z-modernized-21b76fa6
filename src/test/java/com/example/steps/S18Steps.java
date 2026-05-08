package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private Command command;
  private List<com.example.domain.shared.DomainEvent> result;
  private Exception exception;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Handled in When step construction
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Handled in When step construction
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      // Construct command with defaults if valid, otherwise nulls (handled by cmd validation)
      command = new StartSessionCmd("session-123", "teller-1", "term-1");
      result = aggregate.execute(command);
    } catch (Exception e) {
      exception = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(result, "Result should not be null");
    assertEquals(1, result.size());
    assertTrue(result.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent event = (SessionStartedEvent) result.get(0);
    assertEquals("session.started", event.type());
    assertEquals("teller-1", event.tellerId());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
    aggregate = new TellerSessionAggregate("session-violate-auth");
    aggregate.violateAuth();
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_sessions_must_timeout_after_a_configured_period_of_inactivity() {
    aggregate = new TellerSessionAggregate("session-violate-timeout");
    aggregate.violateTimeout();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state_must_accurately_reflect_the_current_operational_context() {
    aggregate = new TellerSessionAggregate("session-violate-nav");
    aggregate.violateNavigation();
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(exception, "Expected an exception to be thrown");
    assertTrue(
        exception instanceof IllegalArgumentException || 
        exception instanceof IllegalStateException ||
        exception instanceof UnknownCommandException,
        "Expected domain error (IllegalArgument/IllegalState/UnknownCommand), but got: " + exception.getClass().getSimpleName()
    );
  }
}
