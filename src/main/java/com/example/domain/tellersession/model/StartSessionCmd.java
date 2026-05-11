package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session following successful authentication.
 * Part of Story S-18: Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {
}
