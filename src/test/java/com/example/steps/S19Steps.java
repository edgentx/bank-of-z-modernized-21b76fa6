package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
  private String sessionId;
  private String menuId;
  private String action;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    this.sessionId = "sess-123";
    // Use test constructor to ensure authenticated state
    this.aggregate = new TellerSessionAggregate(sessionId, "teller-1", true, Instant.now());
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled in Given
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    this.menuId = "MAIN_MENU";
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(sessionId, event.aggregateId());
    assertEquals(menuId, event.menuId());
  }

  // --- Rejection Scenarios ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    this.sessionId = "sess-unauth";
    this.aggregate = new TellerSessionAggregate(sessionId, null, false, Instant.now());
    this.menuId = "MAIN_MENU";
    this.action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    this.sessionId = "sess-timeout";
    // Set last activity to 20 minutes ago (timeout is 15)
    Instant past = Instant.now().minus(Duration.ofMinutes(20));
    this.aggregate = new TellerSessionAggregate(sessionId, "teller-1", true, past);
    this.menuId = "MAIN_MENU";
    this.action = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_context() {
    this.sessionId = "sess-bad-context";
    this.aggregate = new TellerSessionAggregate(sessionId, "teller-1", true, Instant.now());
    // Invalid MenuId
    this.menuId = ""; 
    this.action = "ENTER";
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
  }
}
