package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession aggregate.
 * Handles the lifecycle of a teller's terminal session, including authentication checks,
 * timeout management, and navigation state validation.
 */
public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean active;
    private boolean authenticated; // Derived from auth token validation or external auth service
    private Instant lastActivityAt;
    private String currentState; // Navigation state (e.g., "IDLE", "MENU", "TXN_INPUT")
    private boolean timedOut;

    // Max inactivity in seconds (configurable, hardcoded for domain logic)
    private static final long TIMEOUT_SECONDS = 900; // 15 minutes

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = false;
        this.authenticated = false;
        this.lastActivityAt = Instant.now();
        this.currentState = "INIT";
        this.timedOut = false;
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
        // Scenario: A teller must be authenticated to initiate a session.
        // We assume the command contains the token, and the aggregate (or a domain service)
        // validates it. Here, we assume isAuthenticated() checks the context/token validity.
        if (!isAuthenticated(cmd)) {
            throw new IllegalStateException("Teller must be authenticated to initiate a session.");
        }

        // Scenario: Sessions must timeout after a configured period of inactivity.
        if (isTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Scenario: Navigation state must accurately reflect the current operational context.
        // Assuming we need to be in an IDLE or INIT state to start.
        if (!isNavValid()) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context.");
        }

        var event = new SessionStartedEvent(id(), cmd.tellerId(), cmd.terminalId(), Instant.now());
        apply(event);
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Stub for authentication logic. In a real app, this might verify a JWT or check a flag
    // set by a prior AuthenticateCmd.
    private boolean isAuthenticated(StartSessionCmd cmd) {
        // For the test scenario where violation occurs, this flag would be false.
        return this.authenticated;
    }

    private boolean isTimedOut() {
        if (this.lastActivityAt == null) return false;
        return Instant.now().isAfter(this.lastActivityAt.plusSeconds(TIMEOUT_SECONDS));
    }

    private boolean isNavValid() {
        // Valid states for starting a session: "INIT" or "IDLE"
        return "INIT".equals(this.currentState) || "IDLE".equals(this.currentState);
    }

    // Logic to apply state changes (used by constructor and command handler)
    private void apply(SessionStartedEvent event) {
        this.tellerId = event.tellerId();
        this.terminalId = event.terminalId();
        this.active = true;
        this.currentState = "IDLE"; // Transition to IDLE upon start
        this.lastActivityAt = event.occurredAt();
    }

    // --- Test Setters ---
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    public void setTimedOut(boolean timedOut) { this.timedOut = timedOut; }
    public void setCurrentState(String currentState) { this.currentState = currentState; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    // --- Getters ---
    public String getTellerId() { return tellerId; }
    public String getTerminalId() { return terminalId; }
    public boolean isActive() { return active; }
}