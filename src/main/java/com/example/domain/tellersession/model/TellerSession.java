package com.example.domain.tellersession.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * TellerSession aggregate.
 * Manages state for a Bank Teller's terminal session.
 */
public class TellerSession extends AggregateRoot {
    private final String sessionId;
    private String tellerId;
    private String branchId;
    private boolean active;
    private boolean timedOut;
    private boolean authenticated;
    private boolean navigationStateValid;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.active = false;
        this.timedOut = false;
        this.authenticated = false;
        this.navigationStateValid = true;
    }

    @Override
    public String id() {
        return sessionId;
    }

    // Command Execution
    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EndSessionCmd c) {
            return handleEndSession(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleEndSession(EndSessionCmd cmd) {
        // Invariant: Authenticated
        if (!authenticated) {
            throw new IllegalStateException("Cannot end session: Teller is not authenticated.");
        }

        // Invariant: Not Timed Out
        if (timedOut) {
            throw new IllegalStateException("Cannot end session: Session has timed out due to inactivity.");
        }

        // Invariant: Navigation State Valid
        if (!navigationStateValid) {
            throw new IllegalStateException("Cannot end session: Navigation state is inconsistent with operational context.");
        }

        var event = new SessionEndedEvent(sessionId, tellerId, Instant.now());
        this.active = false;
        this.authenticated = false;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test / Setup helper methods
    public void initializeSession(String tellerId, String branchId) {
        this.tellerId = tellerId;
        this.branchId = branchId;
        this.active = true;
        this.authenticated = true;
    }

    public void forceTimeoutForTesting() {
        this.timedOut = true;
    }

    public void corruptNavigationStateForTesting() {
        this.navigationStateValid = false;
    }
}