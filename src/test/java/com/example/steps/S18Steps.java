package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
  private Exception caughtException;
  private List<DomainEvent> resultEvents;

  // Repository Stub for testing
  static class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private TellerSessionAggregate store;
    @Override public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
      this.store = aggregate;
      return aggregate;
    }
    @Override public Optional<TellerSessionAggregate> findById(String id) {
      return Optional.ofNullable(store);
    }
  }

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Default to authenticated for success case
    repo.save(aggregate);
  }

  @Given("a valid tellerId is provided")
  public void a_valid_tellerId_is_provided() {
    // Implicitly handled in the 'When' step construction
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminalId_is_provided() {
    // Implicitly handled in the 'When' step construction
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-auth-fail");
    // Do NOT mark authenticated
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-timeout");
    aggregate.markAuthenticated();
    aggregate.markTimedOut();
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-nav-state");
    aggregate.markAuthenticated();
    aggregate.markStarted(); // Simulating already started state
    repo.save(aggregate);
  }

  @When("the StartSessionCmd command is executed")
  public void the_StartSessionCmd_command_is_executed() {
    try {
      Command cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    Assertions.assertNotNull(resultEvents);
    Assertions.assertFalse(resultEvents.isEmpty());
    Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    Assertions.assertNotNull(caughtException);
    // Check for specific exception types (IllegalStateException) could be added here
    Assertions.assertTrue(caughtException instanceof IllegalStateException);
  }
}