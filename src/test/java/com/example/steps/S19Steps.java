package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCommand;
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
  private NavigateMenuCommand command;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.authenticate();
    aggregate.setSessionTimeout(Duration.ofMinutes(15));
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_not_authenticated() {
    aggregate = new TellerSessionAggregate("session-401");
    // Intentionally do not authenticate
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_is_expired() {
    aggregate = new TellerSessionAggregate("session-408");
    aggregate.authenticate();
    // Set last activity to 30 minutes ago
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(30)));
    aggregate.setSessionTimeout(Duration.ofMinutes(15));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_with_invalid_context() {
    aggregate = new TellerSessionAggregate("session-400");
    aggregate.authenticate();
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled implicitly in scenario setup or command creation
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled in command creation
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled in command creation
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    try {
      String menuId = "MAIN_MENU";
      if (aggregate.id().equals("session-400")) {
        // Context violation
        menuId = ""; 
      }
      command = new NavigateMenuCommand(aggregate.id(), menuId, "ENTER");
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
    assertEquals("menu.navigated", event.type());
    assertEquals("MAIN_MENU", event.targetMenuId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException);
    assertTrue(thrownException instanceof IllegalStateException);
  }
}
