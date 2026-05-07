package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId required");
        }
        if (menuId == null || menuId.isBlank()) {
            throw new IllegalArgumentException("menuId required");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action required");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavigateMenuCmd that = (NavigateMenuCmd) o;
        return Objects.equals(sessionId, that.sessionId) && Objects.equals(menuId, that.menuId) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, menuId, action);
    }
}