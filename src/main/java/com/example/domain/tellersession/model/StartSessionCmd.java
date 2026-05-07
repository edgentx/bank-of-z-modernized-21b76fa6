package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Context: S-18 (TellerSession)
 */
public record StartSessionCmd(
    String tellerSessionId,
    String tellerId,
    String terminalId
) implements Command {}