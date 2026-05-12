package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd cmd;
  private Exception thrownException;
  private List<DomainEvent> resultingEvents;

  @And("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // Prepared in 'When' step
  }

  @And("a valid menuId is provided")
  public void a_valid_menu_id_is_provided() {
    // Prepared in 'When' step
  }

  @And("a valid action is provided")
  public void a_valid_action_is_provided() {
    // Prepared in 'When' step
  }

  @When("the NavigateMenuCmd command is executed")
  public void the_navigate_menu_cmd_command_is_executed() {
    if (aggregate == null) {
      aggregate = new TellerSessionAggregate("session-123");
    }
    cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
    try {
      resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(thrownException, "Should not throw exception");
    assertNotNull(resultingEvents, "Events should not be null");
    assertEquals(1, resultingEvents.size(), "Should emit one event");
    assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent, "Event type mismatch");

    MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals("session-123", event.sessionId());
    assertEquals("MAIN_MENU", event.menuId());
    assertEquals("ENTER", event.action());
  }
}
