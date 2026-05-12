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

/**
 * Story-specific step definitions for S-19 (NavigateMenuCmd).
 * The TellerSession aggregate + Givens come from
 * {@link TellerSessionSharedSteps} / {@link TellerSessionSharedContext}.
 */
public class S19Steps {

  private final TellerSessionSharedContext ctx;
  private final ScenarioContext sc;

  public S19Steps(TellerSessionSharedContext ctx, ScenarioContext sc) {
    this.ctx = ctx;
    this.sc = sc;
  }

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
    TellerSessionAggregate aggregate = ctx.aggregate;
    if (aggregate == null) {
      aggregate = new TellerSessionAggregate("session-123");
      ctx.aggregate = aggregate;
    }
    NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
    try {
      ctx.resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      sc.thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void a_menu_navigated_event_is_emitted() {
    assertNull(sc.thrownException, "Should not throw exception");
    List<DomainEvent> events = ctx.resultingEvents;
    assertNotNull(events, "Events should not be null");
    assertEquals(1, events.size(), "Should emit one event");
    assertTrue(events.get(0) instanceof MenuNavigatedEvent, "Event type mismatch");

    MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals(ctx.aggregate.id(), event.sessionId());
    assertEquals("MAIN_MENU", event.menuId());
    assertEquals("ENTER", event.action());
  }
}
