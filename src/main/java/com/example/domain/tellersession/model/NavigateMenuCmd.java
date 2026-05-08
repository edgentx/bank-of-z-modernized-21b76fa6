package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be blank");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        if (menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be blank");
        Objects.requireNonNull(action, "action cannot be null");
    }
}