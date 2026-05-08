package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private List<com.example.domain.shared.DomainEvent> events;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    this.sessionId = "session-123";
    this.aggregate = new TellerSessionAggregate(sessionId);
    // Simulate authentication and session setup to ensure valid state
    aggregate.setAuthenticated(true);
    aggregate.setLastActivity(Instant.now());
    aggregate.setCurrentMenuId("MAIN_MENU");
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // sessionId is already initialized in the aggregate constructor context
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    this.menuId = "ACCOUNT_DETAILS";
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
      events = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    Assertions.assertNotNull(events);
    Assertions.assertEquals(1, events.size());
    Assertions.assertEquals("menu.navigated", events.get(0).type());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    this.sessionId = "session-401";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.setAuthenticated(false); // Violation: Not authenticated
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    this.sessionId = "session-408";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.setAuthenticated(true);
    // Violation: Last activity was 30 minutes ago
    aggregate.setLastActivity(Instant.now().minusSeconds(1800)); 
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_nav_state() {
    this.sessionId = "session-conflict";
    this.aggregate = new TellerSessionAggregate(sessionId);
    aggregate.setAuthenticated(true);
    // Violation: Simulate a context where navigation is invalid or locked
    aggregate.setNavigationLocked(true);
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    Assertions.assertNotNull(caughtException);
    // We expect an IllegalArgumentException or IllegalStateException, or a custom DomainException
    Assertions.assertTrue(IllegalArgumentException.class.isInstance(caughtException) 
        || IllegalStateException.class.isInstance(caughtException));
  }
}