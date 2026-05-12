package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.util.List;

/**
 * TellerSessionAggregate — user-interface-navigation bounded context.
 * Owns the lifecycle of an authenticated teller's terminal session.
 *
 * Invariants enforced on StartSessionCmd (BANK S-18):
 *   1. Teller must be authenticated before a session can begin.
 *   2. Any prior session must NOT be in a timed-out state lingering on the aggregate.
 *   3. Navigation state must be coherent (not flagged as inconsistent with operational context).
 *
 * Invariants enforced on NavigateMenuCmd (BANK S-19):
 *   1. Teller must be authenticated to initiate/continue a session.
 *   2. Session must not be timed out from inactivity.
 *   3. Navigation state must accurately reflect the current operational context.
 */
public class TellerSessionAggregate extends AggregateRoot {
  private final String id;
  private boolean authenticated = true;
  private boolean timedOut = false;
  private boolean navigationStateValid = true;
  private Status status = Status.NONE;

  public enum Status { NONE, ACTIVE }

  public TellerSessionAggregate(String id) { this.id = id; }

  @Override public String id() { return id; }

  public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
  public void setTimedOut(boolean timedOut) { this.timedOut = timedOut; }
  public void setNavigationStateValid(boolean navigationStateValid) { this.navigationStateValid = navigationStateValid; }

  public boolean isAuthenticated() { return authenticated; }
  public boolean isTimedOut() { return timedOut; }
  public boolean isNavigationStateValid() { return navigationStateValid; }
  public Status getStatus() { return status; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof StartSessionCmd c) return startSession(c);
    if (cmd instanceof NavigateMenuCmd c) return navigateMenu(c);
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> startSession(StartSessionCmd c) {
    if (!authenticated) {
      throw new IllegalStateException("Cannot start session: teller is not authenticated");
    }
    if (timedOut) {
      throw new IllegalStateException("Cannot start session: prior session is in timed-out state and must be cleared");
    }
    if (!navigationStateValid) {
      throw new IllegalStateException("Cannot start session: navigation state does not reflect current operational context");
    }
    if (status == Status.ACTIVE) {
      throw new IllegalStateException("Cannot start session: a session is already active");
    }
    var event = SessionStartedEvent.create(id, c.tellerId(), c.terminalId());
    this.status = Status.ACTIVE;
    addEvent(event);
    incrementVersion();
    return List.of(event);
  }

  private List<DomainEvent> navigateMenu(NavigateMenuCmd c) {
    if (!authenticated) {
      throw new IllegalStateException("Cannot navigate menu: teller is not authenticated");
    }
    if (timedOut) {
      throw new IllegalStateException("Cannot navigate menu: session has timed out due to inactivity");
    }
    if (!navigationStateValid) {
      throw new IllegalStateException("Cannot navigate menu: navigation state does not reflect current operational context");
    }
    var event = MenuNavigatedEvent.create(id, c.sessionId(), c.menuId(), c.action());
    addEvent(event);
    incrementVersion();
    return List.of(event);
  }
}
