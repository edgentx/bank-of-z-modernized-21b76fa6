package com.example.domain.tellersession;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** BANK S-18 — StartSessionCmd on TellerSession aggregate. */
class TellerSessionAggregateTest {

  @Test void startSessionHappyPathEmitsSessionStartedEvent() {
    var agg = new TellerSessionAggregate("session-1");
    List<DomainEvent> events = agg.execute(new StartSessionCmd("teller-1", "terminal-1"));
    assertEquals(1, events.size());
    assertInstanceOf(SessionStartedEvent.class, events.get(0));
    var event = (SessionStartedEvent) events.get(0);
    assertEquals("session-1", event.aggregateId());
    assertEquals("teller-1", event.tellerId());
    assertEquals("terminal-1", event.terminalId());
    assertEquals(TellerSessionAggregate.Status.ACTIVE, agg.getStatus());
  }

  @Test void startSessionRejectedWhenTellerNotAuthenticated() {
    var agg = new TellerSessionAggregate("session-auth");
    agg.setAuthenticated(false);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new StartSessionCmd("teller-1", "terminal-1")));
  }

  @Test void startSessionRejectedWhenPriorSessionTimedOut() {
    var agg = new TellerSessionAggregate("session-to");
    agg.setTimedOut(true);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new StartSessionCmd("teller-1", "terminal-1")));
  }

  @Test void startSessionRejectedWhenNavigationStateInvalid() {
    var agg = new TellerSessionAggregate("session-nav");
    agg.setNavigationStateValid(false);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new StartSessionCmd("teller-1", "terminal-1")));
  }

  @Test void startSessionRejectedWhenAlreadyActive() {
    var agg = new TellerSessionAggregate("session-dup");
    agg.execute(new StartSessionCmd("teller-1", "terminal-1"));
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new StartSessionCmd("teller-1", "terminal-1")));
  }

  @Test void startSessionCmdRejectsBlankTellerId() {
    assertThrows(IllegalArgumentException.class,
        () -> new StartSessionCmd("", "terminal-1"));
  }

  @Test void startSessionCmdRejectsBlankTerminalId() {
    assertThrows(IllegalArgumentException.class,
        () -> new StartSessionCmd("teller-1", ""));
  }
}
