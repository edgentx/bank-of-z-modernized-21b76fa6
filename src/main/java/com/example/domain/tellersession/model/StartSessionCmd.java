package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session on a specific terminal.
 * @param tellerId The ID of the authenticated teller.
 * @param terminalId The ID of the terminal where the session is starting.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
