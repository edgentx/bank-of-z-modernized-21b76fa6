package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller session.
 * S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(String tellerId, String terminalId, boolean isAuthenticated) implements Command {
}