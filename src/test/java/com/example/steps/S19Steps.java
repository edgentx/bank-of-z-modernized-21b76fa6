package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private Throwable thrownException;
  private List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    sessionId = "sess-123";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(); // Default to valid/authenticated state
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    sessionId = "sess-123";
  }

  @Given("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    menuId = "MAIN_MENU";
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    action = "SELECT";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(thrownException, "Should not have thrown an exception");
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(sessionId, event.aggregateId());
    assertEquals(menuId, event.menuId());
    assertEquals(action, event.action());
  }

  // Negative Scenarios

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    sessionId = "sess-unauth";
    aggregate = new TellerSessionAggregate(sessionId);
    // Do NOT mark authenticated
    menuId = "MAIN_MENU";
    action = "SELECT";
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    sessionId = "sess-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.expireSession(); // Force timeout
    menuId = "MAIN_MENU";
    action = "SELECT";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_context() {
    sessionId = "sess-context";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    menuId = "MAIN_MENU";
    action = ""; // Invalid/Blank action
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException, "Expected an exception to be thrown");
    assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
  }
}
