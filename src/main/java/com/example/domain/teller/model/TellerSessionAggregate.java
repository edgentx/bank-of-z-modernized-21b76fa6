package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class TellerSessionAggregate extends AggregateRoot {
  private final String sessionId;
  private String tellerId;
  private String branchId;
  private boolean active;
  private Instant startedAt;
  private Instant endedAt;

  public TellerSessionAggregate(String sessionId) {
    this.sessionId = sessionId;
  }

  @Override public String id() { return sessionId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof StartTellerSessionCmd c) {
      if (active) throw new IllegalStateException("Session already active: " + c.sessionId());
      if (c.tellerId() == null || c.tellerId().isBlank()) throw new IllegalArgumentException("tellerId required");
      if (c.branchId() == null || c.branchId().isBlank()) throw new IllegalArgumentException("branchId required");
      var now = Instant.now();
      var event = new TellerSessionStartedEvent(c.sessionId(), c.tellerId(), c.branchId(), now);
      this.tellerId = c.tellerId();
      this.branchId = c.branchId();
      this.startedAt = now;
      this.active = true;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    if (cmd instanceof EndTellerSessionCmd c) {
      if (!active) throw new IllegalStateException("Session not active: " + c.sessionId());
      var now = Instant.now();
      var event = new TellerSessionEndedEvent(c.sessionId(), this.tellerId, now);
      this.endedAt = now;
      this.active = false;
      addEvent(event);
      incrementVersion();
      return List.of(event);
    }
    throw new UnknownCommandException(cmd);
  }

  public boolean isActive() { return active; }
  public String getTellerId() { return tellerId; }
  public String getBranchId() { return branchId; }
  public Instant getStartedAt() { return startedAt; }
  public Instant getEndedAt() { return endedAt; }
}
