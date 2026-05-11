package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  // In-Memory Repository Stub
  private static class InMemoryRepo implements TellerSessionRepository {
    @Override
    public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
      return aggregate; // No-op for BDD
    }
    @Override
    public TellerSessionAggregate findById(String id) { return null; }
  }

  private final TellerSessionRepository repo = new InMemoryRepo();

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated("teller-01"); // Default valid state
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateWithUnauthenticatedTeller() {
    aggregate = new TellerSessionAggregate("session-401");
    aggregate.markUnauthenticated(); // Violates authenticated invariant
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatIsTimedOut() {
    aggregate = new TellerSessionAggregate("session-408");
    aggregate.markAuthenticated("teller-01");
    aggregate.markExpired(); // Violates timeout invariant
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateWithInvalidNavigation() {
    aggregate = new TellerSessionAggregate("session-nav-fail");
    aggregate.markAuthenticated("teller-01");
    aggregate.markInvalidNavigationState(); // Violates nav invariant
  }

  @And("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Normally this would involve passing the ID into the command constructor
    // For this BDD, we ensure the aggregate ID is set (done in constructors)
    assertNotNull(aggregate.id());
  }

  @When("the EndSessionCmd command is executed")
  public void theEndSessionCmdCommandIsExecuted() {
    try {
      Command cmd = new EndSessionCmd(aggregate.id());
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void aSessionEndedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    
    SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
    assertEquals("session.ended", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertFalse(aggregate.isActive()); // Side-effect check
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
    assertTrue(thrownException.getMessage().length() > 0);
  }
}
