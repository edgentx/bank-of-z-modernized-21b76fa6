package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Used by the terminal interface after successful AuthN/AuthZ via Spring Security/CICS.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}