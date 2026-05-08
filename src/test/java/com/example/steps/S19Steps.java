package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private Exception capturedException;
  private List<DomainEvent> resultEvents;

  private static final String SESSION_ID = "sess-123";
  private static final String MENU_ID = "MAIN_MENU";
  private static final String ACTION = "ENTER";
  private static final String TELLER_ID = "teller-001";

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    aggregate.markAuthenticated(TELLER_ID);
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    // Intentionally do NOT mark authenticated
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    aggregate.markAuthenticated(TELLER_ID);
    // Force last activity time into the distant past
    aggregate.markTimedOut(); 
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_nav_state() {
    aggregate = new TellerSessionAggregate(SESSION_ID);
    aggregate.markAuthenticated(TELLER_ID);
    // Context logic: The aggregate validates the input.
    // We will provide a blank menuId in the command step to trigger this,
    // or we could simulate a corrupted state here if needed.
    // For this scenario, the violation is triggered by the command inputs, 
    // so we just need a valid aggregate ready to receive a bad command.
  }

  @Given("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled by constants
  }

  @Given("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled by constants
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled by constants
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    long now = Instant.now().toEpochMilli();
    
    // Default to valid data
    String targetMenu = MENU_ID;
    
    // Check context to violate state if needed for the last scenario
    // (Scoping this specific check to the specific violation)
    if (aggregate.getCurrentMenuId() != null && aggregate.getCurrentMenuId().isEmpty()) {
       // Logic placeholder if we were setting internal state, 
       // but here we modify the command input in the specific step context if needed.
       // However, since Cucumber steps are global, we rely on the 'Given' 
       // setting up the context. We'll just use valid defaults here 
       // and catch the exception.
    }

    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, targetMenu, ACTION, TELLER_ID, now);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @When("the NavigateMenuCmd command is executed with invalid context")
  public void the_navigate_menu_cmd_command_is_executed_with_invalid_context() {
    long now = Instant.now().toEpochMilli();
    try {
      // Intentionally violate the invariant: blank menuId
      NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, "", ACTION, TELLER_ID, now);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(capturedException, "Should not have thrown an exception");
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals(SESSION_ID, event.aggregateId());
    assertEquals(MENU_ID, event.menuId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Exception should be thrown for domain violation");
    // In Java DDD, domain violations are often IllegalStateExceptions or IllegalArgumentExceptions
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
  }
}
