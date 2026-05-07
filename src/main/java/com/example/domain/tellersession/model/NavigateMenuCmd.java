package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be null");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null");
    }
}
