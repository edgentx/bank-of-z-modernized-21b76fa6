package com.example.domain.tellersession;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** BANK S-20 — EndSessionCmd on TellerSession aggregate. */
class TellerSessionEndSessionTest {

  @Test void endSessionHappyPathEmitsSessionEndedEvent() {
    var agg = new TellerSessionAggregate("session-20");
    List<DomainEvent> events = agg.execute(new EndSessionCmd("session-20"));
    assertEquals(1, events.size());
    assertInstanceOf(SessionEndedEvent.class, events.get(0));
    var event = (SessionEndedEvent) events.get(0);
    assertEquals("session.ended", event.type());
    assertEquals("session-20", event.aggregateId());
    assertEquals("session-20", event.sessionId());
    assertEquals(TellerSessionAggregate.Status.ENDED, agg.getStatus());
  }

  @Test void endSessionRejectedWhenTellerNotAuthenticated() {
    var agg = new TellerSessionAggregate("session-end-auth");
    agg.setAuthenticated(false);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new EndSessionCmd("session-end-auth")));
  }

  @Test void endSessionRejectedWhenSessionTimedOut() {
    var agg = new TellerSessionAggregate("session-end-to");
    agg.setTimedOut(true);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new EndSessionCmd("session-end-to")));
  }

  @Test void endSessionRejectedWhenNavigationStateInvalid() {
    var agg = new TellerSessionAggregate("session-end-nav");
    agg.setNavigationStateValid(false);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new EndSessionCmd("session-end-nav")));
  }

  @Test void endSessionCmdRejectsBlankSessionId() {
    assertThrows(IllegalArgumentException.class,
        () -> new EndSessionCmd(""));
  }

  @Test void endSessionCmdRejectsNullSessionId() {
    assertThrows(IllegalArgumentException.class,
        () -> new EndSessionCmd(null));
  }
}
