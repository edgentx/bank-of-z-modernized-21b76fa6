package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to start a new Teller Session.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean authenticated
) implements Command {}
