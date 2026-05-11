package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: TellerSession Navigation.
 */
public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd command;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  // 15 minutes in millis to match Aggregate logic
  private static final long TIMEOUT_MS = Duration.ofMinutes(15).toMillis();

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    String sessionId = "session-123";
    aggregate = new TellerSessionAggregate(sessionId);
    // Ensure it is authenticated for the happy path
    aggregate.markAuthenticated("teller-01");
    aggregate.setLastActivityAt(Instant.now());
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled in constructor setup, but we ensure the command uses the aggregate ID
    // Command construction happens in 'When'
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Command construction happens in 'When'
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Command construction happens in 'When'
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      // Default happy path values if not overridden by specific violation scenarios
      String sid = aggregate != null ? aggregate.id() : "session-unknown";
      long activity = aggregate != null ? Instant.now().toEpochMilli() : System.currentTimeMillis();
      
      // If command wasn't prepared with specific violation data, prepare a valid one
      if (command == null) {
        command = new NavigateMenuCmd(sid, "MAIN_MENU", "ENTER", "teller-01", activity);
      }
      
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents, "Expected events to be emitted");
    assertEquals(1, resultEvents.size(), "Expected exactly one event");
    
    DomainEvent event = resultEvents.get(0);
    assertTrue(event instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    
    MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
    assertEquals("menu.navigated", navEvent.type());
    assertEquals(aggregate.id(), navEvent.aggregateId());
    assertEquals("MAIN_MENU", navEvent.menuId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException, "Expected an exception to be thrown");
    assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
  }

  // --- Scenarios for Violations ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    String sessionId = "session-auth-fail";
    aggregate = new TellerSessionAggregate(sessionId);
    // Explicitly do not authenticate
    aggregate.deauthenticate(); 
    
    command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER", null, System.currentTimeMillis());
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    String sessionId = "session-timeout-fail";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-01");
    
    // Set last activity to 16 minutes ago (exceeds 15 min timeout)
    Instant past = Instant.now().minus(Duration.ofMinutes(16));
    aggregate.setLastActivityAt(past);
    
    // Command claims current time, but aggregate state is old
    command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER", "teller-01", System.currentTimeMillis());
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation_context() {
    String sessionId = "session-context-fail";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-01");
    aggregate.setLastActivityAt(Instant.now());
    
    // Using action "INVALID_CONTEXT" triggers the check in the aggregate
    command = new NavigateMenuCmd(sessionId, "MAIN_MENU", "INVALID_CONTEXT", "teller-01", System.currentTimeMillis());
  }
}
