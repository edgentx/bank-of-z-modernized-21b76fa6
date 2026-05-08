package com.example.domain.tellersession.command;

import com.example.domain.shared.Command;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isStaleContext,
        boolean isNavigationStateValid
) implements Command {
}