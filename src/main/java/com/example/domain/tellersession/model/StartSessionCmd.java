package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
