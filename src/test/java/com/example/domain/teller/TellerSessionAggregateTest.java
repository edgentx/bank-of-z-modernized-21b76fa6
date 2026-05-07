package com.example.domain.teller;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TDD Test Suite for S-18: TellerSession Aggregate.
 * Tests the StartSessionCmd command and SessionStartedEvent event.
 */
class TellerSessionAggregateTest {

    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_3270_A";
    private static final String SESSION_ID = "SESSION_123";

    private TellerSession aggregate;

    @BeforeEach
    void setUp() {
        // Create a fresh aggregate for each test.
        // The aggregate constructor logic will be implemented in the Green phase.
        aggregate = new TellerSession(SESSION_ID);
    }

    @Test
    @DisplayName("Scenario: Successfully execute StartSessionCmd")
    void testSuccessfulExecution() {
        // Given a valid TellerSession aggregate
        // And a valid tellerId is provided
        // And a valid terminalId is provided
        StartSessionCmd cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);

        // When the StartSessionCmd command is executed
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then a session.started event is emitted
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(SessionStartedEvent.class);

        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertThat(event.type()).isEqualTo("session.started");
        assertThat(event.aggregateId()).isEqualTo(SESSION_ID);
        assertThat(event.tellerId()).isEqualTo(VALID_TELLER_ID);
        assertThat(event.terminalId()).isEqualTo(VALID_TERMINAL_ID);
        assertThat(event.occurredAt()).isNotNull();
        assertThat(event.occurredAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Scenario: StartSessionCmd rejected — Teller must be authenticated")
    void testAuthenticationRequired() {
        // Given a TellerSession aggregate that violates authentication
        // Simulating an unauthenticated state (e.g., via constructor or state manipulation)
        TellerSession unauthenticatedAggregate = new TellerSession(SESSION_ID, false, true, true);

        StartSessionCmd cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);

        // When the StartSessionCmd command is executed
        // Then the command is rejected with a domain error
        assertThatThrownBy(() -> unauthenticatedAggregate.execute(cmd))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("authenticated");
    }

    @Test
    @DisplayName("Scenario: StartSessionCmd rejected — Sessions must timeout after inactivity")
    void testTimeoutInvariant() {
        // Given a TellerSession aggregate that violates timeout constraints
        // (e.g., marked as stale/timed out)
        TellerSession staleAggregate = new TellerSession(SESSION_ID, true, false, true);

        StartSessionCmd cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);

        // When the StartSessionCmd command is executed
        // Then the command is rejected with a domain error
        assertThatThrownBy(() -> staleAggregate.execute(cmd))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("timeout")
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("Scenario: StartSessionCmd rejected — Navigation state must reflect context")
    void testNavigationStateInvariant() {
        // Given a TellerSession aggregate that violates navigation state integrity
        // (e.g., terminal is in an invalid state for a session start)
        TellerSession badNavAggregate = new TellerSession(SESSION_ID, true, true, false);

        StartSessionCmd cmd = new StartSessionCmd(VALID_TELLER_ID, VALID_TERMINAL_ID);

        // When the StartSessionCmd command is executed
        // Then the command is rejected with a domain error
        assertThatThrownBy(() -> badNavAggregate.execute(cmd))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("navigation");
    }

    @Test
    @DisplayName("Feature: Unknown commands should be rejected")
    void testUnknownCommand() {
        // Given an arbitrary command that isn't StartSessionCmd
        Object unknownCmd = new Object() {};

        // When executed
        // Then it should throw UnknownCommandException
        assertThrows(UnknownCommandException.class, () -> aggregate.execute((com.example.domain.shared.Command) unknownCmd));
    }
}
