package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
  private TellerSessionAggregate aggregate;
  private Exception capturedException;
  private List<DomainEvent> resultEvents;

  static class InMemoryTellerSessionRepository implements TellerSessionRepository {
    private TellerSessionAggregate store;
    @Override public void save(TellerSessionAggregate aggregate) { this.store = aggregate; }
    @Override public Optional<TellerSessionAggregate> findById(String id) {
      return Optional.ofNullable(store);
    }
  }

  // Given steps

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    // Setup valid state: authenticated, recent activity, valid nav state
    aggregate.markAuthenticated(); 
    aggregate.setLastActivity(java.time.Instant.now());
    aggregate.setNavigationState("HOME");
    repository.save(aggregate);
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled by the aggregate setup in the previous step
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate("session-auth-fail");
    // Violate: not authenticated
    repository.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate("session-timeout-fail");
    aggregate.markAuthenticated();
    // Violate: last activity was 31 minutes ago (Timeout is 30)
    aggregate.setLastActivity(java.time.Instant.now().minusSeconds(1860));
    repository.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationState() {
    aggregate = new TellerSessionAggregate("session-nav-fail");
    aggregate.markAuthenticated();
    aggregate.setLastActivity(java.time.Instant.now());
    // Violate: Navigation state is MENU_OPEN (invalid for EndSession)
    aggregate.setNavigationState("MENU_OPEN");
    repository.save(aggregate);
  }

  // When steps

  @When("the EndSessionCmd command is executed")
  public void theEndSessionCmdCommandIsExecuted() {
    Command cmd = new EndSessionCmd(aggregate.id());
    try {
      // Reload aggregate from repository to simulate persistence behavior
      TellerSessionAggregate agg = repository.findById(aggregate.id()).orElseThrow();
      resultEvents = agg.execute(cmd);
      repository.save(agg);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  // Then steps

  @Then("a session.ended event is emitted")
  public void aSessionEndedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
