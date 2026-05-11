package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd command;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(30));
    aggregate.markAuthenticated(); // Ensure valid state
  }

  @Given("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled in command creation below
  }

  @Given("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled in command creation below
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled in command creation below
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("session-123", event.aggregateId());
    assertEquals("MAIN_MENU", event.menuId());
    assertEquals("ENTER", event.action());
    assertEquals("menu.navigated", event.type());
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-unauth", Duration.ofMinutes(30));
    // markAuthenticated() is NOT called
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-timeout", Duration.ofMinutes(30));
    aggregate.markAuthenticated(); // Start valid
    aggregate.markExpired();       // Force expiration
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_context() {
    aggregate = new TellerSessionAggregate("session-locked", Duration.ofMinutes(30));
    aggregate.markAuthenticated();
    aggregate.setCurrentMenu("LOCKED"); // Set state that prevents navigation
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
    // Verify no events were emitted
    assertNull(resultEvents);
  }
}
