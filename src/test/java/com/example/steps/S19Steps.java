package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd command;
  private List<DomainEvent> resultEvents;
  private Throwable thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Ensure valid state by default
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_auth() {
    aggregate = new TellerSessionAggregate("session-456");
    // Defaults to authenticated = false
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    aggregate = new TellerSessionAggregate("session-789");
    aggregate.markAuthenticated();
    aggregate.markStale(); // Force timeout condition
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_context() {
    aggregate = new TellerSessionAggregate("session-101");
    aggregate.markAuthenticated();
    aggregate.setInvalidContext("BATCH_MODE"); // Simulate incompatible context
  }

  @And("a valid sessionId is provided")
  public void a_valid_sessionId_is_provided() {
    // Session ID is implicitly handled by the aggregate constructor in these steps
  }

  @And("a valid menuId is provided")
  public void a_valid_menuId_is_provided() {
    // Handled in command construction
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Handled in command construction
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_NavigateMenuCmd_command_is_executed() {
    command = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
    try {
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

    var event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertEquals("MAIN_MENU", event.menuId());
    assertNull(thrownException, "Expected no exception, but got: " + thrownException);
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(thrownException, "Expected a domain exception but none was thrown");
    // Specific message checks are implicitly handled by the logic throwing the exact string
    assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    assertNull(resultEvents, "Expected no events to be emitted during failure");
  }
}
