package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd cmd;
  private List<com.example.domain.shared.DomainEvent> resultEvents;
  private Throwable capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Ensure valid base state for success case
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled implicitly by the aggregate constructor ID, 
    // but we ensure the command uses it.
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled in When step
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled in When step
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(capturedException, "Expected success but got exception: " + capturedException);
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertEquals("MAIN_MENU", event.menuId());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-unauth");
    // isAuthenticated defaults to false
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-timeout");
    aggregate.markAuthenticated(); // Must be auth to check timeout
    aggregate.markExpired();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-corrupt");
    aggregate.markAuthenticated();
    aggregate.corruptNavigationState();
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Expected exception but command succeeded");
    assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
  }
}
