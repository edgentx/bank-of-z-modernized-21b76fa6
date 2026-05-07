package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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
  public void a_valid_TellerSession_aggregate() {
    sessionId = "TS-123";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.configureAuthentication(true);
    aggregate.configureSessionExpiry(false); // Active
    aggregate.configureOperationalContext(true);
  }

  @Given("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled in aggregate initialization
    assertNotNull(sessionId);
  }

  @Given("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    menuId = "MAIN_MENU";
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(sessionId, event.aggregateId());
    assertEquals(menuId, event.menuId());
    assertEquals(action, event.action());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    sessionId = "TS-UNAUTH";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.configureAuthentication(false); // Violation
    aggregate.configureSessionExpiry(false);
    aggregate.configureOperationalContext(true);
    
    menuId = "MAIN_MENU";
    action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    sessionId = "TS-TIMEOUT";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.configureAuthentication(true);
    aggregate.configureSessionExpiry(true); // Violation
    aggregate.configureOperationalContext(true);

    menuId = "MAIN_MENU";
    action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_context() {
    sessionId = "TS-CONTEXT";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.configureAuthentication(true);
    aggregate.configureSessionExpiry(false);
    aggregate.configureOperationalContext(false); // Violation

    menuId = "MAIN_MENU";
    action = "ENTER";
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
  }
}
