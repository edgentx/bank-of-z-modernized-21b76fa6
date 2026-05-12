package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-20 (EndSessionCmd).
 * The TellerSession aggregate + Givens come from
 * {@link TellerSessionSharedSteps} / {@link TellerSessionSharedContext}.
 */
public class S20Steps {

  private final TellerSessionSharedContext ctx;
  private final ScenarioContext sc;

  public S20Steps(TellerSessionSharedContext ctx, ScenarioContext sc) {
    this.ctx = ctx;
    this.sc = sc;
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    TellerSessionAggregate aggregate = ctx.aggregate;
    if (aggregate == null) {
      aggregate = new TellerSessionAggregate("session-123");
      ctx.aggregate = aggregate;
    }
    EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
    try {
      ctx.resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      sc.thrownException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNull(sc.thrownException, "Should not throw exception");
    List<DomainEvent> events = ctx.resultingEvents;
    assertNotNull(events, "Events should not be null");
    assertEquals(1, events.size(), "Should emit one event");
    assertTrue(events.get(0) instanceof SessionEndedEvent, "Event type mismatch");

    SessionEndedEvent event = (SessionEndedEvent) events.get(0);
    assertEquals("session.ended", event.type());
    assertEquals(ctx.aggregate.id(), event.aggregateId());
    assertEquals(ctx.aggregate.id(), event.sessionId());
  }
}
