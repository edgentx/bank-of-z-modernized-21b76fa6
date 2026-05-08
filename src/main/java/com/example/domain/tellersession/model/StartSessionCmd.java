package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean authenticated,   // Represents successful auth
        boolean timedOut,        // Represents a timeout context violation
        boolean navigationStateValid // Represents nav context validity
) implements Command {}
