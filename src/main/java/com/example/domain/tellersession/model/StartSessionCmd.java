package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * @param sessionId The unique identifier for the session instance.
 * @param tellerId  The authenticated teller's ID.
 * @param terminalId The terminal ID from which the session is initiated.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
