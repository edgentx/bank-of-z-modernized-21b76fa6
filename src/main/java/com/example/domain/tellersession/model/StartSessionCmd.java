package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Validated by TellerSession aggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
