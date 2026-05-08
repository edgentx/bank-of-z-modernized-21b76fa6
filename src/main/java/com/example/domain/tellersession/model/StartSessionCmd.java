package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Story: S-18
 */
public record StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated) implements Command {
}
