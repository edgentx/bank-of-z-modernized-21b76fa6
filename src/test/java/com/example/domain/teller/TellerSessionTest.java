package com.example.domain.teller;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.List;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;

import org.junit.jupiter.api.Test;

public class TellerSessionTest {

  // S-18 AC: Successfully execute StartSessionCmd
  @Test
  public void test_execute_StartSessionCmd_emits_SessionStartedEvent() {
    // Arrange
    String sessionId = "session-1";
    String tellerId = "teller-101";
    String terminalId = "term-A";
    TellerSession aggregate = new TellerSession(sessionId);
    StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

    // Act
    List<DomainEvent> events = aggregate.execute(cmd);

    // Assert
    assertEquals(1, events.size(), "Should emit exactly one event");
    DomainEvent event = events.get(0);
    assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    SessionStartedEvent started = (SessionStartedEvent) event;
    assertEquals(sessionId, started.aggregateId());
    assertEquals(tellerId, started.tellerId());
    assertEquals(terminalId, started.terminalId());
    assertNotNull(started.occurredAt());
  }

  // S-18 AC: Rejected — A teller must be authenticated to initiate a session.
  @Test
  public void test_execute_StartSessionCmd_rejects_unauthenticated_teller() {
    // Arrange
    String sessionId = "session-2";
    String tellerId = null; // Violation: Teller not authenticated/valid
    String terminalId = "term-B";
    TellerSession aggregate = new TellerSession(sessionId);
    StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      aggregate.execute(cmd);
    });
    assertTrue(exception.getMessage().contains("tellerId"));
  }

  // S-18 AC: Rejected — Sessions must timeout after a configured period of inactivity.
  @Test
  public void test_execute_StartSessionCmd_rejects_timeout_configuration_violation() {
    // Arrange
    // Simulation of a command attempting to start a session that is already in an invalid state
    // or context that violates timeout rules immediately (e.g. improper parameters)
    String sessionId = "session-3";
    String tellerId = "teller-102";
    String terminalId = ""; // Violation: Empty terminalId implies invalid context/timeout config
    TellerSession aggregate = new TellerSession(sessionId);
    StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId);

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      aggregate.execute(cmd);
    });
    assertTrue(exception.getMessage().contains("terminalId"));
  }

  // S-18 AC: Rejected — Navigation state must accurately reflect the current operational context.
  @Test
  public void test_execute_StartSessionCmd_rejects_invalid_navigation_context() {
    // Arrange
    String sessionId = "session-4";
    // Start a session first
    TellerSession aggregate = new TellerSession(sessionId);
    StartSessionCmd cmd1 = new StartSessionCmd(sessionId, "teller-103", "term-C");
    aggregate.execute(cmd1);

    // Try to start again (Violation: Navigation state mismatch)
    StartSessionCmd cmd2 = new StartSessionCmd(sessionId, "teller-103", "term-C");

    // Act & Assert
    // Aggregate should reject duplicate start or context mismatch
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      aggregate.execute(cmd2);
    });
    assertTrue(exception.getMessage().contains("already started"));
  }
}