package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 */
public record InitiateTellerSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}