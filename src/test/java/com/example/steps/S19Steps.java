package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.MenuNavigatedEvent;
import com.example.domain.userinterface.model.NavigateMenuCmd;
import com.example.domain.userinterface.model.TellerSessionAggregate;
import com.example.domain.userinterface.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
  private String sessionId = "session-1";
  private String menuId = "MAIN_MENU";
  private String action = "SELECT";
  private Exception caughtException;
  private List<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-123");
    repo.save(aggregate);
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Handled by setup
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled by setup
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled by setup
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
    try {
      // Reload to ensure clean state from repo if needed, though here we use instance directly
      resultEvents = aggregate.execute(cmd);
      repo.save(aggregate);
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
    assertEquals(sessionId, event.aggregateId());
    assertEquals(menuId, event.menuId());
    assertEquals("menu.navigated", event.type());
  }

  // Negative Scenarios

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate(sessionId);
    // Do not authenticate
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-123");
    aggregate.violateTimeout();
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_context() {
    aggregate = new TellerSessionAggregate(sessionId);
    aggregate.markAuthenticated("teller-123");
    // Force internal state to invalid context (simulated)
    aggregate.violateContext();
    repo.save(aggregate);
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
  }
}
