package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.ui.model.*;
import com.example.domain.ui.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
  private TellerSessionAggregate aggregate;
  private List<com.example.domain.shared.DomainEvent> events;
  private Exception caughtException;

  static class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private final List<TellerSessionAggregate> store = new ArrayList<>();
    @Override public void save(TellerSessionAggregate a) { store.add(a); }
    @Override public Optional<TellerSessionAggregate> findById(String id) { return store.stream().filter(x -> x.id().equals(id)).findFirst(); }
  }

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-1");
    aggregate.markAuthenticated("teller-1", "terminal-1");
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateNotAuthenticated() {
    aggregate = new TellerSessionAggregate("session-2");
    // Do not mark authenticated
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateTimedOut() {
    aggregate = new TellerSessionAggregate("session-3");
    aggregate.markAuthenticated("teller-3", "terminal-3");
    aggregate.markTimedOut();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateInvalidNav() {
    aggregate = new TellerSessionAggregate("session-4");
    aggregate.markAuthenticated("teller-4", "terminal-4");
    aggregate.markNavStateInvalid();
  }

  @And("a valid tellerId is provided")
  public void aValidTellerIdIsProvided() {
    // Setup is handled in the aggregate setup for simplicity
  }

  @And("a valid terminalId is provided")
  public void aValidTerminalIdIsProvided() {
    // Setup is handled in the aggregate setup for simplicity
  }

  @When("the StartSessionCmd command is executed")
  public void theStartSessionCmdCommandIsExecuted() {
    // Using defaults for the successful path, hardcoded for violations
    String tId = (aggregate.getTellerId() != null) ? aggregate.getTellerId() : "teller-1";
    String termId = (aggregate.getTerminalId() != null) ? aggregate.getTerminalId() : "terminal-1";

    var cmd = new StartSessionCmd(tId, termId, true); // isAuthenticated check inside aggregate depends on state, cmd flag passes if true
    try {
      events = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void aSessionStartedEventIsEmitted() {
    assertNotNull(events);
    assertEquals(1, events.size());
    assertTrue(events.get(0) instanceof SessionStartedEvent);
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
  }
}
