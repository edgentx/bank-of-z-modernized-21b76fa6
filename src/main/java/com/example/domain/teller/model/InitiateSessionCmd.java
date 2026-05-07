package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Context: S-19 TellerSession Aggregate Lifecycle.
 */
public record InitiateSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
