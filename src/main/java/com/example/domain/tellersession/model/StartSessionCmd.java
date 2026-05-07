package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a teller session.
 */
public record StartSessionCmd(
    String sessionId,
    String terminalId,
    String tellerId
) implements Command {}
