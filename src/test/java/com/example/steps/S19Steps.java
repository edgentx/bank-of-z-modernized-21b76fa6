package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private Exception capturedException;
  private String sessionId = "session-123";

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate(sessionId);
    // Defaults: Authenticated and Active
    aggregate.markAuthenticated();
    aggregate.resetLastActivity();
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(sessionId);
    // Do not authenticate
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.markExpired(); // Force timeout
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.markTerminated(); // Invalid state
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled by setup
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled by command creation in 'When'
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled by command creation in 'When'
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
      aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(aggregate.uncommittedEvents());
    assertFalse(aggregate.uncommittedEvents().isEmpty());
    DomainEvent event = aggregate.uncommittedEvents().get(0);
    assertEquals("menu.navigated", event.type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
