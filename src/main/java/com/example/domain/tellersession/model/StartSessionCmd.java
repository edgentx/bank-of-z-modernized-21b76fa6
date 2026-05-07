package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * @param sessionId The unique ID of the session.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal where the session is started.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
