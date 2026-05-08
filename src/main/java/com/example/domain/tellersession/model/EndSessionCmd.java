package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * S-20: Terminates the teller session and clears sensitive session state.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
