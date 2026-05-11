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
  private NavigateMenuCmd command;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  // --- Scenario 1: Success ---

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    // Use factory to create a pre-hydrated, authenticated session
    aggregate = TellerSessionAggregate.createAuthenticated("session-123", "MAIN_MENU");
  }

  @And("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Session ID is implicit in the aggregate creation above, checked here.
    assertNotNull(aggregate.id());
  }

  @And("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Setup for command creation
  }

  @And("a valid action is provided")
  public void aValidActionIsProvided() {
    // Setup for command creation
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      command = new NavigateMenuCmd("session-123", "ACCOUNT_INQUIRY", "ENTER");
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNull(caughtException, "Should not have thrown exception");
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("session-123", event.aggregateId());
    assertEquals("ACCOUNT_INQUIRY", event.menuId());
  }

  // --- Scenario 2: Auth Rejection ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate("session-401");
    aggregate.setAuthenticated(false); // Explicitly unauthenticated
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(caughtException);
    assertTrue(caughtException.getMessage().contains("Authentication required"));
  }

  // --- Scenario 3: Timeout Rejection ---

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = TellerSessionAggregate.createAuthenticated("session-timeout", "MAIN_MENU");
    // Set last activity to 20 minutes ago
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
  }

  // --- Scenario 4: Context/State Rejection ---

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationState() {
    aggregate = TellerSessionAggregate.createAuthenticated("session-state", "DEPOSIT_SCREEN");
    // We are already at DEPOSIT_SCREEN
    aggregate.setCurrentMenuId("DEPOSIT_SCREEN");
  }

  // Note: We reuse the When/Then from above (Cucumber matches step text). 
  // We just need to ensure the command triggers the specific violation logic.
  
  // Override the command creation for Scenario 4
  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted_StateViolation() {
    // If we are in the specific context of Scenario 4 (checking state violation)
    if ("session-state".equals(aggregate.id())) {
       try {
         // Command to navigate to the same screen with action 'refresh' (simulated invalid context)
         command = new NavigateMenuCmd("session-state", "DEPOSIT_SCREEN", "refresh");
         resultEvents = aggregate.execute(command);
       } catch (Exception e) {
         caughtException = e;
       }
    } else {
       // Delegate to main executor for other scenarios to avoid step definition ambiguity
       // In a real runner, we might split these, but for this file structure we check context.
       theNavigateMenuCmdCommandIsExecuted();
    }
  }

}
