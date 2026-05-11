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

import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private Exception thrownException;
  private List<DomainEvent> resultEvents;

  // Scenario: Successfully execute NavigateMenuCmd
  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    // Create an authenticated session with recent activity
    aggregate = new TellerSessionAggregate("session-123", true, Instant.now(), "MAIN_MENU");
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    this.sessionId = "session-123";
  }

  @And("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    this.menuId = "ACCOUNT_INQUIRY";
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
      this.resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      this.thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals("ACCOUNT_INQUIRY", event.targetMenuId());
    assertEquals("ENTER", event.action());
  }

  // Scenario: NavigateMenuCmd rejected — Authenticated
  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    // Create an unauthenticated session
    aggregate = new TellerSessionAggregate("session-401", false, Instant.now(), "MAIN_MENU");
    this.sessionId = "session-401";
    this.menuId = "ACCOUNT_INQUIRY";
    this.action = "ENTER";
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
    assertTrue(thrownException.getMessage().contains("authenticated"));
  }

  // Scenario: NavigateMenuCmd rejected — Timeout
  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    // Create an authenticated session with old activity timestamp (e.g., 20 minutes ago)
    Instant oldTime = Instant.now().minusSeconds(20 * 60);
    aggregate = new TellerSessionAggregate("session-408", true, oldTime, "MAIN_MENU");
    this.sessionId = "session-408";
    this.menuId = "ACCOUNT_INQUIRY";
    this.action = "ENTER";
  }

  // Scenario: NavigateMenuCmd rejected — Navigation Context
  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_context() {
    // Create a session. Trying to navigate to the menu you are already at violates the context rule
    // defined in the aggregate logic for this scenario.
    aggregate = new TellerSessionAggregate("session-409", true, Instant.now(), "MAIN_MENU");
    this.sessionId = "session-409";
    this.menuId = "MAIN_MENU"; // Trying to navigate to where we already are
    this.action = "ENTER";
  }
}