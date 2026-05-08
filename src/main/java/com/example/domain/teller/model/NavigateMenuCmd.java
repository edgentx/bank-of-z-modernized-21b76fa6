package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal interface to a specific menu or screen.
 * Mirrors legacy 3270 'Enter' or PF-key actions.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {

    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null");
        if (targetMenuId == null || targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId cannot be null");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null");
    }
}
