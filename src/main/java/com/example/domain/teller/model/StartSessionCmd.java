package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to start a Teller Session.
 * ID: S-18.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant timestamp,
        String navigationState // e.g. "HOME", "MENU"
) implements Command {
}
