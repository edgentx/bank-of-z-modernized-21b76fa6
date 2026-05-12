package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-18 (StartSessionCmd).
 * Shared TellerSession Given steps live in {@link TellerSessionSharedSteps};
 * scenario state is shared via {@link TellerSessionSharedContext}; the
 * "the command is rejected with a domain error" assertion lives in
 * {@link CommonSteps}.
 */
public class S18Steps {

  private final TellerSessionSharedContext ctx;
  private final ScenarioContext sc;

  public S18Steps(TellerSessionSharedContext ctx, ScenarioContext sc) {
    this.ctx = ctx;
    this.sc = sc;
  }

  @When("the StartSessionCmd command is executed")
  public void the_start_session_cmd_command_is_executed() {
    StartSessionCmd cmd = new StartSessionCmd("teller-1", "terminal-1");
    try {
      ctx.resultingEvents = ctx.aggregate.execute(cmd);
    } catch (Exception e) {
      sc.thrownException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNull(sc.thrownException, "Should not throw exception");
    List<DomainEvent> events = ctx.resultingEvents;
    assertNotNull(events, "Events should not be null");
    assertEquals(1, events.size(), "Should emit one event");
    assertTrue(events.get(0) instanceof SessionStartedEvent, "Event type mismatch");

    SessionStartedEvent event = (SessionStartedEvent) events.get(0);
    assertEquals("session-123", event.aggregateId());
    assertEquals("teller-1", event.tellerId());
    assertEquals("terminal-1", event.terminalId());
  }
}
