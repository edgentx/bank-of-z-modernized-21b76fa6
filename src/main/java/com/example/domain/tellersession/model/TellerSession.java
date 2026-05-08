package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private TellerSessionState state;
    private String currentNavigationContext;
    private long lastActivityTimestamp;

    // Configuration for timeout (in milliseconds). In a real app, this might be injected or static.
    private static final long SESSION_TIMEOUT_MS = 15 * 60 * 1000; // 15 minutes

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.state = TellerSessionState.NOT_AUTHENTICATED;
        this.currentNavigationContext = "UNKNOWN";
        this.lastActivityTimestamp = System.currentTimeMillis();
    }

    @Override
    public String id() {
        return sessionId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return endSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> endSession(EndSessionCmd cmd) {
        // Invariant: A teller must be authenticated to initiate a session.
        // Context: Interpreted as requiring an active/authenticated session to perform actions like explicit termination.
        if (this.state != TellerSessionState.AUTHENTICATED) {
            throw new IllegalStateException("Cannot end session: Session is not active or authenticated.");
        }

        // Invariant: Sessions must timeout after a configured period of inactivity.
        // We check if the session is effectively timed out before allowing an explicit end command.
        long now = System.currentTimeMillis();
        if (now - this.lastActivityTimestamp > SESSION_TIMEOUT_MS) {
            throw new IllegalStateException("Cannot end session: Session has already timed out due to inactivity.");
        }

        // Invariant: Navigation state must accurately reflect the current operational context.
        // If the system detected a nav error, we might block clean termination to force investigation.
        if ("UNKNOWN".equals(this.currentNavigationContext) || this.currentNavigationContext.startsWith("error")) {
             throw new IllegalStateException("Cannot end session: Navigation state is invalid or error context detected.");
        }

        var event = new SessionEndedEvent(this.sessionId, Instant.now());
        
        // Apply state changes
        this.state = TellerSessionState.NOT_AUTHENTICATED; // Or a specific TERMINATED state
        this.currentNavigationContext = "TERMINATED";
        this.lastActivityTimestamp = now;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Test helper to set internal state for BDD scenarios.
     * In a real application, state evolves via Events (apply methods).
     */
    public void initializeState(TellerSessionState state, String navContext, long lastActivity) {
        this.state = state;
        this.currentNavigationContext = navContext;
        this.lastActivityTimestamp = lastActivity;
    }
}
