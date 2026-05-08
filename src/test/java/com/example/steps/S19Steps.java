package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    String sessionId = "session-" + Instant.now().toEpochMilli();
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(); // Ensure valid state for success case
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_auth() {
    String sessionId = "session-violation-auth";
    aggregate = new TellerSessionAggregate(sessionId);
    // Defaults to unauthenticated
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    String sessionId = "session-violation-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(); // Must be valid otherwise
    aggregate.markExpired(); // Force timeout
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_nav_state() {
    String sessionId = "session-violation-state";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    aggregate.breakNavigationState(); // Corrupt state
  }

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // Handled implicitly by aggregate creation, but could validate here if needed
    assertNotNull(aggregate.id());
  }

  @And("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    // Validated in Command construction
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Validated in Command construction
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(capturedException, "Should not have thrown exception");
    assertNotNull(resultEvents, "Events list should not be null");
    assertEquals(1, resultEvents.size(), "Should emit exactly one event");
    assertEquals("menu.navigated", resultEvents.get(0).type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Should have thrown an exception");
    assertTrue(capturedException instanceof IllegalStateException);
    // We don't check exact message content in BDD to avoid fragility, just type
  }
}
