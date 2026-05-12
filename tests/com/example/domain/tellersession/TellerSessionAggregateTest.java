package com.example.domain.tellersession;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** BANK S-18/S-19 — TellerSession aggregate. */
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

  // --- S-19 NavigateMenuCmd ---

  @Test void navigateMenuHappyPathEmitsMenuNavigatedEvent() {
    var agg = new TellerSessionAggregate("session-9");
    List<DomainEvent> events = agg.execute(new NavigateMenuCmd("session-9", "MAIN_MENU", "ENTER"));
    assertEquals(1, events.size());
    assertInstanceOf(MenuNavigatedEvent.class, events.get(0));
    var event = (MenuNavigatedEvent) events.get(0);
    assertEquals("menu.navigated", event.type());
    assertEquals("session-9", event.aggregateId());
    assertEquals("session-9", event.sessionId());
    assertEquals("MAIN_MENU", event.menuId());
    assertEquals("ENTER", event.action());
  }

  @Test void navigateMenuRejectedWhenTellerNotAuthenticated() {
    var agg = new TellerSessionAggregate("session-nm-auth");
    agg.setAuthenticated(false);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new NavigateMenuCmd("session-nm-auth", "MAIN_MENU", "ENTER")));
  }

  @Test void navigateMenuRejectedWhenSessionTimedOut() {
    var agg = new TellerSessionAggregate("session-nm-to");
    agg.setTimedOut(true);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new NavigateMenuCmd("session-nm-to", "MAIN_MENU", "ENTER")));
  }

  @Test void navigateMenuRejectedWhenNavigationStateInvalid() {
    var agg = new TellerSessionAggregate("session-nm-nav");
    agg.setNavigationStateValid(false);
    assertThrows(IllegalStateException.class,
        () -> agg.execute(new NavigateMenuCmd("session-nm-nav", "MAIN_MENU", "ENTER")));
  }

  @Test void navigateMenuCmdRejectsBlankSessionId() {
    assertThrows(IllegalArgumentException.class,
        () -> new NavigateMenuCmd("", "MAIN_MENU", "ENTER"));
  }

  @Test void navigateMenuCmdRejectsBlankMenuId() {
    assertThrows(IllegalArgumentException.class,
        () -> new NavigateMenuCmd("session-1", "", "ENTER"));
  }

  @Test void navigateMenuCmdRejectsBlankAction() {
    assertThrows(IllegalArgumentException.class,
        () -> new NavigateMenuCmd("session-1", "MAIN_MENU", ""));
  }
}
