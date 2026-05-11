package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * @param sessionId The unique identifier for the session aggregate.
 * @param tellerId The authenticated ID of the teller.
 * @param terminalId The ID of the terminal where the session is starting.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
