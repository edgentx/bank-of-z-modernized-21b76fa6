package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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

  // --- Givens ---

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    this.sessionId = "SESSION-123";
    this.aggregate = new TellerSessionAggregate(sessionId);
    // Default valid state
    aggregate.markAuthenticated();
    aggregate.setCurrentScreenContext("MainMenu"); // Valid context
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled in constructor step, explicitly ensuring it's not null
    assertNotNull(this.sessionId);
  }

  @Given("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    this.menuId = "MainMenu";
  }

  @Given("a valid action is provided")
  public void aValidActionIsProvided() {
    this.action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuth() {
    this.sessionId = "SESSION-401";
    this.aggregate = new TellerSessionAggregate(sessionId);
    // NOT calling markAuthenticated()
    this.menuId = "MainMenu";
    this.action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatIsTimedOut() {
    this.sessionId = "SESSION-408";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Set last activity to 20 minutes ago
    aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    this.menuId = "MainMenu";
    this.action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationContext() {
    this.sessionId = "SESSION-409";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Set context to MainMenu
    aggregate.setCurrentScreenContext("MainMenu");
    // Try to jump to a deep detail screen
    this.menuId = "Transaction_Detail";
    this.action = "ENTER";
  }

  // --- Whens ---

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
      this.resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      this.capturedException = e;
    }
  }

  // --- Thens ---

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNotNull(resultEvents, "Events should not be null");
    assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event must be MenuNavigatedEvent");
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(menuId, event.targetMenuId());
    assertEquals(sessionId, event.aggregateId());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException, "An exception should have been thrown");
    assertTrue(capturedException instanceof IllegalStateException, "Exception must be IllegalStateException");
    
    String message = capturedException.getMessage();
    assertTrue(
        message.contains("authenticated") || 
        message.contains("timeout") || 
        message.contains("context"),
        "Error message should match the violated invariant: " + message
    );
  }
}
