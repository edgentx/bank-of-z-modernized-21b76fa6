package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Validated and processed by TellerSessionAggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
