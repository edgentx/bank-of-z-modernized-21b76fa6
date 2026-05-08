package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Used by TellerSessionAggregate.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    String navigationContext,
    boolean isAuthenticated
) implements Command {
}