package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal UI to a new menu or screen.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {

    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (targetMenuId == null || targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }

    public String sessionId() { return sessionId; }
    public String targetMenuId() { return targetMenuId; }
    public String action() { return action; }
}
