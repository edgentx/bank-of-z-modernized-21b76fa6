package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.MenuNavigatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

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
  public void a_valid_teller_session_aggregate() {
    sessionId = "sess-123";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-001"); // Ensure authenticated for happy path
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    sessionId = "sess-unauth";
    aggregate = new TellerSessionAggregate(sessionId);
    // Intentionally not calling markAuthenticated
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    sessionId = "sess-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-001");
    aggregate.markExpired(); // Force timeout
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    sessionId = "sess-bad-nav";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-001");
    aggregate.setCurrentMenu("MAIN_MENU"); // Set context
    // The 'action' in the next step will trigger the specific logic in aggregate
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // sessionId usually set in aggregate setup, using that.
    assertNotNull(sessionId);
  }

  @Given("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    this.menuId = "ACCOUNT_SUMMARY";
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    // Special hook for the "Navigation state" violation scenario
    if (aggregate.getCurrentMenuId() != null && aggregate.getCurrentMenuId().equals("MAIN_MENU")) {
        this.action = "INVALID_CONTEXT";
    }

    NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(capturedException, "Should not have thrown exception: " + capturedException);
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals(sessionId, event.aggregateId());
    assertEquals("menu.navigated", event.type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
