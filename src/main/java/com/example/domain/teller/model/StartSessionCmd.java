package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal being used.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
