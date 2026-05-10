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
  private Command command;
  private Exception caughtException;
  private java.util.List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
  }

  @Given("a valid tellerId is provided")
  public void a_valid_teller_id_is_provided() {
    // Handled in When block construction
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminal_id_is_provided() {
    // Handled in When block construction
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_auth() {
    aggregate = new TellerSessionAggregate("session-456");
    // We simulate this violation by passing null/blank tellerId in the command later
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-789");
    aggregate.expireSession(); // Force state where it is active but last activity was too long ago
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation() {
    aggregate = new TellerSessionAggregate("session-101");
    aggregate.corruptNavigationState("TRANSACTION_SCREEN"); // Corrupt state
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    try {
      // Determine ID based on context (simulated)
      String id = aggregate.id();
      // For the auth failure scenario, we pass a null tellerId
      String teller = id.equals("session-456") ? null : "teller-1";
      command = new StartSessionCmd(id, teller, "terminal-1");
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    assertEquals("session.started", resultEvents.get(0).type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    // We expect either IllegalStateException or IllegalArgumentException based on invariants
    assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
  }
}
