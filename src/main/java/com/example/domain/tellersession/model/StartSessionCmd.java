package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal where the session is started.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
