package com.example.domain.teller.model;

import java.time.Instant;

/**
 * Value object / State holder for Teller Session properties.
 * Separated from the Aggregate Root for cleaner handling of state data.
 */
public class TellerSession {
    private final String sessionId;
    private String tellerId;
    private boolean isAuthenticated;
    private boolean isActive;
    private Instant lastActivityAt;
    private boolean isNavigationConsistent;

    public TellerSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastActivityAt = Instant.now();
        this.isNavigationConsistent = true;
    }

    public String sessionId() { return sessionId; }
    public String tellerId() { return tellerId; }
    public boolean isAuthenticated() { return isAuthenticated; }
    public boolean isActive() { return isActive; }
    public Instant lastActivityAt() { return lastActivityAt; }
    public boolean isNavigationConsistent() { return isNavigationConsistent; }

    public void authenticate(String tellerId) {
        this.tellerId = tellerId;
        this.isAuthenticated = true;
        this.isActive = true;
    }

    public void updateActivity(Instant time) {
        this.lastActivityAt = time;
    }

    public void terminate() {
        this.isActive = false;
        this.isAuthenticated = false;
    }

    public void markNavigationInconsistent() {
        this.isNavigationConsistent = false;
    }
}
