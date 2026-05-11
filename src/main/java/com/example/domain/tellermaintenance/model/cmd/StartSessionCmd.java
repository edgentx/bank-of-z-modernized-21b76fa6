package com.example.domain.tellermaintenance.model.cmd;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        int timeoutInSeconds,
        String navigationContext
) implements Command {}
