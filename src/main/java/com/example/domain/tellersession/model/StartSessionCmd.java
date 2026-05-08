package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 * @param sessionId The unique identifier for the session.
 * @param tellerId  The unique identifier for the teller.
 * @param terminalId The unique identifier for the terminal.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}