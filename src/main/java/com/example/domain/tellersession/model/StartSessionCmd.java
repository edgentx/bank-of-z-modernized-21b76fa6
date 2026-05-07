package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new teller session.
 * @param sessionId The unique ID of the session.
 * @param tellerId The authenticated ID of the teller.
 * @param terminalId The ID of the terminal being used.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
