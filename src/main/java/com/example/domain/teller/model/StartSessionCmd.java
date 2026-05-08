package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 * S-18: Implement StartSessionCmd
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated,
    boolean timedOut,
    boolean navigationStateInvalid
) implements Command {

    // Helper to extract tellerId for cleaner code in aggregate
    public String tellerId() { return tellerId; }
    public String terminalId() { return terminalId; }
    public boolean isAuthenticated() { return authenticated; }
    public boolean isTimedOut() { return timedOut; }
    public boolean isNavigationStateInvalid() { return navigationStateInvalid; }
}
