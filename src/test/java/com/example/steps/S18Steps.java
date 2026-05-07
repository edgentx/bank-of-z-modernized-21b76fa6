package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
  private StartSessionCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-1");
  }

  @Given("a valid tellerId is provided")
  public void aValidTellerIdIsProvided() {
    // Nop, handled in command construction in 'When'
  }

  @Given("a valid terminalId is provided")
  public void aValidTerminalIdIsProvided() {
    // Nop, handled in command construction in 'When'
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate("session-auth-fail");
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate("session-timeout-fail");
    aggregate.simulateStaleState(); // Force the timestamp back to trigger invariant
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigation() {
    aggregate = new TellerSessionAggregate("session-nav-fail");
  }

  @When("the StartSessionCmd command is executed")
  public void theStartSessionCmdCommandIsExecuted() {
    // Determine command data based on the scenario context (implied by Gherkin Given)
    // Defaults
    String id = aggregate.id();
    String teller = "teller-123";
    String terminal = "TERM-01";
    boolean auth = true;

    // Overrides based on Gherkin context states
    if (id.equals("session-auth-fail")) auth = false;
    if (id.equals("session-nav-fail")) terminal = "INVALID-TERM";
    // Timeout is handled by aggregate state modification in the Given

    cmd = new StartSessionCmd(id, teller, terminal, auth);
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void aSessionStartedEventIsEmitted() {
    assertNull(caughtException, "Expected no error, but got: " + caughtException);
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session.started", event.type());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(caughtException, "Expected a domain error but command succeeded");
    assertTrue(
      caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
      "Exception should be a domain rule violation (IllegalStateException or IllegalArgumentException), but was: " + caughtException.getClass()
    );
  }
}
