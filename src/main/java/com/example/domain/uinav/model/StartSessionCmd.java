package com.example.domain.uinav.model;

import com.example.domain.shared.Command;

/**
 * Command to start a teller session.
 * POJO / Record implementation.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId
) implements Command {}
