package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 * S-18: user-interface-navigation.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        long timeoutMinutes,
        String navigationState
) implements Command {
}
