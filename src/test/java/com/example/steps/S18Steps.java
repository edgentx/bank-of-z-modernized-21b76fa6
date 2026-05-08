package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId = "session-123";
  private String tellerId = "teller-01";
  private String terminalId = "term-05";
  private Exception capturedException;
  private StartSessionCmd command;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate(sessionId);
  }

  @And("a valid tellerId is provided")
  public void aValidTellerIdIsProvided() {
    // Defaults are fine
  }

  @And("a valid terminalId is provided")
  public void aValidTerminalIdIsProvided() {
    // Defaults are fine
  }

  @When("the StartSessionCmd command is executed")
  public void theStartSessionCmdCommandIsExecuted() {
    try {
      command = new StartSessionCmd(sessionId, tellerId, terminalId);
      aggregate.execute(command);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void aSessionStartedEventIsEmitted() {
    assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    var events = aggregate.uncommittedEvents();
    assertFalse(events.isEmpty(), "Expected events to be emitted");
    assertEquals(1, events.size(), "Expected exactly one event");
    DomainEvent event = events.get(0);
    assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
    assertEquals("session.started", event.type());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markUnauthenticated();
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markTimedOut();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationState() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markInvalidNavigation();
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException, "Expected an exception to be thrown");
    assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
  }
}
