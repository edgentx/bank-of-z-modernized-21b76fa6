package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Set;

/**
 * Command to initiate a new Teller Session.
 * Enforces invariants: Authentication, Terminal Validation, Session State, Timeouts.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    Set<String> activeRoles,
    String navigationState
) implements Command {}
