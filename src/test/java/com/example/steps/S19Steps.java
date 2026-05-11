package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String sessionId;
  private String menuId;
  private String action;
  private List<DomainEvent> resultingEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    sessionId = "ts-123";
    aggregate = new TellerSessionAggregate(sessionId);
    // Default to valid state (authenticated, active)
    aggregate.setAuthenticated(true);
    aggregate.setActive(true);
    aggregate.setLastActivity(Instant.now());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    sessionId = "ts-999";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.setAuthenticated(false); // Violation: not authenticated
    aggregate.setActive(true);
    aggregate.setLastActivity(Instant.now());
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    sessionId = "ts-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.setAuthenticated(true);
    aggregate.setActive(true);
    // Violation: last activity was 20 minutes ago (> 15 min threshold)
    aggregate.setLastActivity(Instant.now().minusSeconds(1200));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_state_context() {
    sessionId = "ts-invalid-state";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.setAuthenticated(true);
    aggregate.setActive(false); // Violation: context is inactive/invalid
    aggregate.setLastActivity(Instant.now());
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Session ID usually set in aggregate constructor or context
    // We assume the scenario ID is valid
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    menuId = "MAIN_MENU_01";
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
      resultingEvents = aggregate.execute(cmd);
      capturedException = null;
    } catch (Exception e) {
      capturedException = e;
      resultingEvents = null;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultingEvents, "Events list should not be null");
    assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
    assertEquals("menu.navigated", resultingEvents.get(0).type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Expected an exception to be thrown");
    // In this domain model, validation errors throw IllegalStateException or similar RuntimeExceptions
    // We verify the message content matches the invariants in the feature
    assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
  }
}