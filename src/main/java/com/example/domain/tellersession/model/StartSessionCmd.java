package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Must be executed following successful authentication.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
