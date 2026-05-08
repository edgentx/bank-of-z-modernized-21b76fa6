package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * @param tellerId The ID of the teller initiating the session.
 * @param terminalId The ID of the terminal being used.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {}
