package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private EndSessionCmd cmd;
  private Exception thrownException;
  private List<DomainEvent> resultingEvents;

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    if (aggregate == null) {
      aggregate = new TellerSessionAggregate("session-123");
    }
    cmd = new EndSessionCmd("session-123");
    try {
      resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNull(thrownException, "Should not throw exception");
    assertNotNull(resultingEvents, "Events should not be null");
    assertEquals(1, resultingEvents.size(), "Should emit one event");
    assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent, "Event type mismatch");

    SessionEndedEvent event = (SessionEndedEvent) resultingEvents.get(0);
    assertEquals("session.ended", event.type());
    assertEquals("session-123", event.aggregateId());
    assertEquals("session-123", event.sessionId());
  }
}
