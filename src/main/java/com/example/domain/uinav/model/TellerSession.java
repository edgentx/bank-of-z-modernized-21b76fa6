package com.example.domain.uinav.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class TellerSession extends AggregateRoot {

    private final String sessionId;
    private boolean authenticated = false;
    private boolean active = false;
    private String tellerId;
    private String terminalId;
    private Instant lastActivityAt;
    private String navigationState = "HOME";

    // Configuration: 15 minutes timeout
    private static final long SESSION_TIMEOUT_MINUTES = 15;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
    }

    @Override
    public String id() {
        return sessionId;
    }

    // Used for test setup to simulate previous step completion
    public void markAuthenticated() {
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
    }

    // Used for test setup to simulate previous step completion
    public void setNavigationState(String state) {
        this.navigationState = state;
    }

    // Used for test setup to simulate previous step completion
    public void authenticate(String tellerId, String terminalId) {
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.authenticated = true;
        this.lastActivityAt = Instant.now();
    }

    // Used for test setup to simulate timeout
    public void expireSession() {
        // Set last activity to well past timeout
        this.lastActivityAt = Instant.now().minusSeconds(SESSION_TIMEOUT_MINUTES * 60 + 1);
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof StartSessionCmd c) {
            return startSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> startSession(StartSessionCmd cmd) {
        // Invariant: Navigation state must accurately reflect current context (Must be HOME to start)
        if (!"HOME".equals(this.navigationState)) {
            throw new IllegalStateException("Navigation state must accurately reflect the current operational context. Expected HOME, found " + navigationState);
        }

        // Invariant: Teller must be authenticated
        if (!authenticated) {
            throw new IllegalStateException("A teller must be authenticated to initiate a session.");
        }

        // Invariant: Session must not have timed out
        if (isSessionTimedOut()) {
            throw new IllegalStateException("Sessions must timeout after a configured period of inactivity.");
        }

        // Validation: IDs match
        if (!cmd.tellerId().equals(this.tellerId)) {
            throw new IllegalArgumentException("Command tellerId does not match authenticated teller");
        }
        if (!cmd.terminalId().equals(this.terminalId)) {
            throw new IllegalArgumentException("Command terminalId does not match authenticated terminal");
        }

        var event = new SessionStartedEvent(this.sessionId, this.tellerId, this.terminalId, Instant.now());
        this.active = true;
        this.lastActivityAt = Instant.now();
        this.addEvent(event);
        this.incrementVersion();
        return List.of(event);
    }

    private boolean isSessionTimedOut() {
        return Instant.now().isAfter(this.lastActivityAt.plusSeconds(SESSION_TIMEOUT_MINUTES * 60));
    }

    public boolean isActive() {
        return active;
    }
}
