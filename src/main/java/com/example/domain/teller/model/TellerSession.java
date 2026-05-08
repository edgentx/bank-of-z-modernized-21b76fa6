package com.example.domain.teller.model;

import com.example.domain.shared.*;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession Aggregate
 * Handles teller terminal authentication and session lifecycle.
 */
public class TellerSession extends AggregateRoot {

  private final String sessionId;
  private String currentTellerId;
  private String currentTerminalId;
  private boolean active;

  public TellerSession(String sessionId) {
    this.sessionId = sessionId;
    this.active = false;
  }

  @Override
  public String id() {
    return sessionId;
  }

  @Override
  public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof StartSessionCmd c) {
      return startSession(c);
    }
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> startSession(StartSessionCmd cmd) {
    // Invariant: Teller must be authenticated (represented by valid ID)
    if (cmd.tellerId() == null || cmd.tellerId().isBlank()) {
      throw new IllegalArgumentException("tellerId required for authentication");
    }

    // Invariant: Operational context (terminal) must be valid and active
    // (modeled here as non-blank terminalId)
    if (cmd.terminalId() == null || cmd.terminalId().isBlank()) {
      throw new IllegalArgumentException("terminalId required for operational context");
    }

    // Invariant: Session state transitions
    if (this.active) {
      throw new IllegalStateException("Session already started. Navigation state mismatch.");
    }

    var event = new SessionStartedEvent(id(), cmd.tellerId(), cmd.terminalId(), Instant.now());
    
    this.currentTellerId = cmd.tellerId();
    this.currentTerminalId = cmd.terminalId();
    this.active = true;

    addEvent(event);
    incrementVersion();
    return List.of(event);
  }

  public boolean isActive() {
    return active;
  }

  public String getCurrentTellerId() {
    return currentTellerId;
  }
}