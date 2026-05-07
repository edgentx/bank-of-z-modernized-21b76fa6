package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (targetMenuId == null || targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }
}
