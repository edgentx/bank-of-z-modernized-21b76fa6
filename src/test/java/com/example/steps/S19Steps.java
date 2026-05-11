package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    this.sessionId = "session-123";
    this.aggregate = new TellerSessionAggregate(sessionId);
    // Setup valid state: authenticated and active
    aggregate.markAuthenticated();
    aggregate.setLastActivityAt(Instant.now());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    this.sessionId = "session-unauth";
    this.aggregate = new TellerSessionAggregate(sessionId);
    // Intentionally do NOT call markAuthenticated().
    // Ensure activity is recent so timeout doesn't trigger first.
    aggregate.setLastActivityAt(Instant.now());
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesSessionTimeout() {
    this.sessionId = "session-timeout";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Set last activity to 20 minutes ago (exceeds 15 min timeout defined in aggregate)
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationContext() {
    this.sessionId = "session-context";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.setLastActivityAt(Instant.now());
    // Set current menu to 'MAIN'
    aggregate.setCurrentMenu("MAIN");
    // The scenario will attempt to navigate to 'MAIN' again
    this.menuId = "MAIN";
  }

  @And("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled in aggregate setup
    assertNotNull(this.sessionId);
  }

  @And("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    if (this.menuId == null) {
      this.menuId = "TRANSACTIONS_MENU";
    }
  }

  @And("a valid action is provided")
  public void aValidActionIsProvided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
      this.resultEvents = aggregate.execute(cmd);
      this.capturedException = null;
    } catch (Exception e) {
      this.capturedException = e;
      this.resultEvents = null;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(sessionId, event.aggregateId());
    assertEquals(menuId, event.menuId());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException);
    // In Java domain logic, we often use RuntimeExceptions (IllegalStateException, IllegalArgumentException) for domain errors.
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
  }
}
