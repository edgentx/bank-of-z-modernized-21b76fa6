package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a teller session.
 * @param sessionId The ID of the session to start.
 * @param tellerId  The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal where the session is starting.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}