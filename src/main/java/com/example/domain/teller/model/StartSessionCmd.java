package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * 
 * @param sessionId Unique ID for the session
 * @param tellerId  ID of the authenticated teller
 * @param terminalId ID of the terminal being used
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}
