package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate to a specific menu or screen.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        // targetMenuId and action validity are context-dependent, checked by aggregate
    }
}