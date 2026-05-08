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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;
  private String sessionId;
  private String menuId;
  private String action;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    sessionId = "sess-123";
    aggregate = new TellerSessionAggregate(sessionId);
    // Hydrate to a valid state
    aggregate.hydrate(
        "teller-1", 
        "MAIN_MENU", 
        true,  // authenticated
        Instant.now(), 
        false  // locked
    );
  }

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // sessionId already set in @Given
    assertNotNull(sessionId);
  }

  @And("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    menuId = "ACCOUNT_INQUIRY";
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    try {
      Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(capturedException, "Should not have thrown exception");
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(menuId, event.menuId());
    assertEquals(action, event.action());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    sessionId = "sess-unauth";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.hydrate(
        "teller-unknown", 
        "LOGIN", 
        false, // NOT authenticated
        Instant.now(), 
        false
    );
    a_valid_menu_id_is_provided();
    a_valid_action_is_provided();
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    sessionId = "sess-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    // Set last activity to 20 minutes ago (Timeout is 15)
    aggregate.hydrate(
        "teller-1", 
        "MAIN_MENU", 
        true, 
        Instant.now().minusSeconds(1200), // 20 mins ago
        false
    );
    a_valid_menu_id_is_provided();
    a_valid_action_is_provided();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_context() {
    sessionId = "sess-locked";
    aggregate = new TellerSessionAggregate(sessionId);
    // Set locked = true
    aggregate.hydrate(
        "teller-1", 
        "MAIN_MENU", 
        true, 
        Instant.now(), 
        true // LOCKED (Invalid Context)
    );
    a_valid_menu_id_is_provided();
    a_valid_action_is_provided();
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException, "Expected an exception to be thrown");
    assertTrue(capturedException instanceof IllegalStateException);
    
    // Verify no events were emitted
    assertNull(resultEvents);
  }
}