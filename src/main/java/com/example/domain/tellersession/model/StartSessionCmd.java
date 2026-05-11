package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to initiate a teller session.
 * S-18: StartSessionCmd.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean authenticated,
        String navigationContext,
        Instant occurredAt
) implements Command {
}