package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current TellerSession.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
