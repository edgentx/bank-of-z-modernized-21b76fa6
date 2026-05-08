package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;
  private static final Duration TIMEOUT = Duration.of(15, ChronoUnit.MINUTES);

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    // Create an authenticated session that hasn't timed out
    aggregate = new TellerSessionAggregate("session-123", TIMEOUT);
    // We need to reach in or reflect to set authenticated=true for the happy path
    // or we assume the constructor creates a valid one. The default constructor I created sets authenticated=false.
    // So I will simulate a valid state by creating a specific "valid" scenario.
    // To do this cleanly without adding setters to the domain model (which breaks immutability often),
    // I'll use the overloaded constructor designed for testing complex states.
    aggregate = new TellerSessionAggregate("session-123", true, Instant.now(), TIMEOUT, "HOME");
  }

  @Given("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled by the aggregate initialization
  }

  @Given("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled in the When step
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled in the When step
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "ACCOUNT_SUMMARY", "VIEW");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    assertEquals("menu.navigated", resultEvents.get(0).type());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-401", false, Instant.now(), TIMEOUT, "LOGIN");
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    // Create an authenticated session, but set last activity to 20 minutes ago (Threshold is 15)
    Instant past = Instant.now().minus(20, ChronoUnit.MINUTES);
    aggregate = new TellerSessionAggregate("session-408", true, past, TIMEOUT, "HOME");
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_context() {
    // We define "BLOCKED" as an invalid context for navigation in the aggregate logic
    aggregate = new TellerSessionAggregate("session-blocked", true, Instant.now(), TIMEOUT, "BLOCKED");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // We check for IllegalStateException which is the standard Domain Error in this codebase style
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
