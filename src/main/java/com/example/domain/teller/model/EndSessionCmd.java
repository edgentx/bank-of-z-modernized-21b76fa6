package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a TellerSession.
 * Part of Story S-20: EndSessionCmd on TellerSession.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
