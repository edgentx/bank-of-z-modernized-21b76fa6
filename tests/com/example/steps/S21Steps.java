package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-21 (RenderScreenCmd).
 * Shared ScreenMap Givens + the rejection @Then live in
 * {@link ScreenMapSharedSteps} / {@link CommonSteps};
 * scenario state is shared via {@link ScreenMapSharedContext}.
 */
public class S21Steps {

  private final ScreenMapSharedContext ctx;
  private final ScenarioContext sc;

  public S21Steps(ScreenMapSharedContext ctx, ScenarioContext sc) {
    this.ctx = ctx;
    this.sc = sc;
  }

  @When("the RenderScreenCmd command is executed")
  public void the_render_screen_cmd_command_is_executed() {
    ScreenMapAggregate aggregate = ctx.aggregate;
    if (aggregate == null) {
      aggregate = new ScreenMapAggregate("screen-map-123");
      ctx.aggregate = aggregate;
    }
    RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), "SCR-LOGIN", "3270");
    try {
      ctx.resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      sc.thrownException = e;
    }
  }

  @Then("a screen.rendered event is emitted")
  public void a_screen_rendered_event_is_emitted() {
    assertNull(sc.thrownException, "Should not throw exception");
    List<DomainEvent> events = ctx.resultingEvents;
    assertNotNull(events, "Events should not be null");
    assertEquals(1, events.size(), "Should emit one event");
    assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Event type mismatch");

    ScreenRenderedEvent event = (ScreenRenderedEvent) events.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals(ctx.aggregate.id(), event.aggregateId());
    assertEquals("SCR-LOGIN", event.screenId());
    assertEquals("3270", event.deviceType());
  }
}
