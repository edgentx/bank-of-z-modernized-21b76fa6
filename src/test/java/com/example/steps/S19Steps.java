package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd cmd;
  private List<DomainEvent> resultingEvents;
  private Exception thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    String sessionId = "sess-" + System.currentTimeMillis();
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated(); // Ensure valid state
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // sessionId handled in aggregate creation
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled in cmd creation
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled in cmd creation
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      // Assume defaults for successful path if not set explicitly by other steps
      if (cmd == null) {
         cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
      }
      resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultingEvents);
    assertEquals(1, resultingEvents.size());
    assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
    
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertNull(thrownException, "Expected no exception, but got: " + thrownException);
  }

  // --- Negative Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    String sessionId = "sess-unauth";
    aggregate = new TellerSessionAggregate(sessionId);
    // Intentionally do NOT mark authenticated
    cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    String sessionId = "sess-timeout";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Set activity to 31 minutes ago
    aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
    cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_context() {
    String sessionId = "sess-context";
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated();
    // Send invalid menuId/action to trigger context violation logic in aggregate
    cmd = new NavigateMenuCmd(sessionId, "", ""); 
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
    // Ensure no events were committed
    assertNull(resultingEvents);
  }
}