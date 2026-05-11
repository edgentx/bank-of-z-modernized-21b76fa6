package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    sessionId = "session-123";
    aggregate = new TellerSessionAggregate(sessionId);
    // Valid state implies authenticated for the success scenario
    aggregate.markAuthenticated();
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // sessionId initialized in the 'Given a valid TellerSession aggregate' step
    assertNotNull(sessionId);
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    menuId = "MAIN_MENU";
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(sessionId, event.aggregateId());
    assertEquals(menuId, event.menuId());
    assertEquals(action, event.action());
  }

  // Negative Scenarios

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    sessionId = "session-unauth";
    aggregate = new TellerSessionAggregate(sessionId);
    // Do NOT mark authenticated
    menuId = "MAIN_MENU";
    action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    sessionId = "session-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Set last activity to 31 minutes ago (Timeout is 30)
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    menuId = "MAIN_MENU";
    action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    sessionId = "session-bad-nav";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    
    // Set current context to a specific menu
    String currentMenu = "ACCOUNT_SCREEN";
    // Force state via a successful navigation command first to set internal state
    aggregate.execute(new NavigateMenuCmd(sessionId, currentMenu, "INITIAL"));
    
    // Try to navigate to the same screen
    this.menuId = currentMenu; 
    this.action = "ENTER";
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    // Checking it's a domain logic exception (IllegalStateException or IllegalArgumentException)
    assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
  }
}
