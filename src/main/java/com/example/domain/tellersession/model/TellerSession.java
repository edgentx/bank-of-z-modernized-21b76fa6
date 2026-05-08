package com.example.domain.tellersession.model;

import java.time.Duration;
import java.time.Instant;

/**
 * Value object/State holder for Teller Session specifics.
 * Encapsulates the rules around session validity and operational state.
 */
public class TellerSession {

    public enum SessionState {
        ACTIVE, LOCKED, TRANSMITTING, IDLE
    }

    private final String tellerId;
    private final SessionState state;
    private final Instant lastActivityAt;
    private final Duration inactivityTimeout;

    public TellerSession(String tellerId, SessionState state, Instant lastActivityAt, Duration inactivityTimeout) {
        this.tellerId = tellerId;
        this.state = state;
        this.lastActivityAt = lastActivityAt;
        this.inactivityTimeout = inactivityTimeout;
    }

    public String getTellerId() {
        return tellerId;
    }

    public SessionState getState() {
        return state;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public Duration getInactivityTimeout() {
        return inactivityTimeout;
    }

    /**
     * Checks if the session is currently valid based on auth and timeout rules.
     */
    public boolean isValid() {
        return isAuthenticated() && !hasTimedOut();
    }

    /**
     * Invariant: A teller must be authenticated.
     */
    public boolean isAuthenticated() {
        return tellerId != null && !tellerId.isBlank();
    }

    /**
     * Invariant: Sessions must timeout after a configured period of inactivity.
     */
    public boolean hasTimedOut() {
        if (lastActivityAt == null || inactivityTimeout == null) return false;
        Instant now = Instant.now(); // In a real system, this would likely be passed in (Clock)
        Duration inactivePeriod = Duration.between(lastActivityAt, now);
        return inactivePeriod.compareTo(inactivityTimeout) > 0;
    }

    /**
     * Invariant: Navigation state must accurately reflect current operational context.
     * Navigation is typically allowed only in ACTIVE or IDLE states, not LOCKED or TRANSMITTING.
     */
    public boolean allowsNavigation() {
        return state == SessionState.ACTIVE || state == SessionState.IDLE;
    }
}
