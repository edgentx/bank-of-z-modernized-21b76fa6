package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to start a Teller Session.
 * ID: S-18
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
