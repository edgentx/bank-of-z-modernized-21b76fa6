package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
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
  private Exception thrownException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    this.sessionId = "sess-123";
    this.aggregate = new TellerSessionAggregate(this.sessionId);
    // Initialize state to valid
    this.aggregate.markAuthenticated();
    this.aggregate.updateHeartbeat(Instant.now());
    this.aggregate.allowNavigation();
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    this.sessionId = "sess-123";
  }

  @Given("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    this.menuId = "MAIN_MENU";
  }

  @Given("a valid action is provided")
  public void a_valid_action_is_provided() {
    this.action = "ENTER";
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
      this.resultEvents = this.aggregate.execute(cmd);
    } catch (Exception e) {
      this.thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNotNull(this.resultEvents);
    assertEquals(1, this.resultEvents.size());
    assertTrue(this.resultEvents.get(0) instanceof MenuNavigatedEvent);
    MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(this.sessionId, event.aggregateId());
    assertEquals(this.menuId, event.menuId());
    assertEquals(this.action, event.action());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    this.sessionId = "sess-unauth";
    this.aggregate = new TellerSessionAggregate(this.sessionId);
    // Do NOT mark authenticated
    this.aggregate.updateHeartbeat(Instant.now());
    this.aggregate.allowNavigation();
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    this.sessionId = "sess-timeout";
    this.aggregate = new TellerSessionAggregate(this.sessionId);
    this.aggregate.markAuthenticated();
    // Set last activity to 30 minutes ago (assuming timeout is 15m)
    this.aggregate.updateHeartbeat(Instant.now().minus(Duration.ofMinutes(30)));
    this.aggregate.allowNavigation();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_context() {
    this.sessionId = "sess-context";
    this.aggregate = new TellerSessionAggregate(this.sessionId);
    this.aggregate.markAuthenticated();
    this.aggregate.updateHeartbeat(Instant.now());
    // Do NOT allow navigation (e.g., in a transaction that cannot be interrupted)
    this.aggregate.restrictNavigation();
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(this.thrownException);
    assertTrue(this.thrownException instanceof IllegalStateException || this.thrownException instanceof IllegalArgumentException);
  }
}
