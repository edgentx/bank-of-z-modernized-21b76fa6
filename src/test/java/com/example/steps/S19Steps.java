package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private Exception thrownException;
  private List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    this.sessionId = "session-123";
    // Setup a valid aggregate: authenticated, recent activity, active
    this.aggregate = new TellerSessionAggregate(sessionId);
    this.aggregate.setAuthenticated(true);
    this.aggregate.setLastActivityAt(Instant.now());
    this.aggregate.setActive(true);
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    this.sessionId = "session-123";
  }

  @Given("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    this.menuId = "MAIN_MENU";
  }

  @Given("a valid action is provided")
  public void aValidActionIsProvided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(menuId, action);
      this.resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      this.thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    Assertions.assertNull(thrownException, "Should not have thrown an exception");
    Assertions.assertNotNull(resultEvents);
    Assertions.assertEquals(1, resultEvents.size());
    DomainEvent event = resultEvents.get(0);
    Assertions.assertTrue(event instanceof MenuNavigatedEvent);
    MenuNavigatedEvent navigatedEvent = (MenuNavigatedEvent) event;
    Assertions.assertEquals("MenuNavigatedEvent", navigatedEvent.type());
    Assertions.assertEquals(sessionId, navigatedEvent.aggregateId());
    Assertions.assertEquals(menuId, navigatedEvent.targetMenuId());
    Assertions.assertEquals(action, navigatedEvent.action());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    this.sessionId = "session-unauth";
    this.menuId = "MAIN_MENU";
    this.action = "ENTER";
    // Create aggregate that is NOT authenticated
    this.aggregate = new TellerSessionAggregate(sessionId);
    this.aggregate.setAuthenticated(false); // Violation
    this.aggregate.setActive(true);
    this.aggregate.setLastActivityAt(Instant.now());
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    this.sessionId = "session-timeout";
    this.menuId = "MAIN_MENU";
    this.action = "ENTER";
    this.aggregate = new TellerSessionAggregate(sessionId);
    this.aggregate.setAuthenticated(true);
    this.aggregate.setActive(true);
    // Set last activity to 20 minutes ago (threshold is 15)
    this.aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesOperationalContext() {
    this.sessionId = "session-inactive";
    this.menuId = "MAIN_MENU";
    this.action = "ENTER";
    this.aggregate = new TellerSessionAggregate(sessionId);
    this.aggregate.setAuthenticated(true);
    this.aggregate.setActive(false); // Violation: not active
    this.aggregate.setLastActivityAt(Instant.now());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
    Assertions.assertTrue(thrownException instanceof IllegalStateException);
    // Optional: Verify specific message based on scenario if needed, but type check is sufficient for BDD
  }
}
