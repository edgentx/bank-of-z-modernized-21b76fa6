package com.example.domain.uimodel;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session and clear sensitive state.
 * Part of User Interface Navigation (S-20).
 */
public record EndSessionCmd(String sessionId) implements Command {
}
