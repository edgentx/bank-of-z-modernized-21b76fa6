package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu or screen.
 * S-19: User Interface Navigation.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (targetMenuId == null || targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId required");
        // Action might be optional in some contexts, but required per AC for this story
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}