package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action
) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null) throw new IllegalArgumentException("sessionId required");
        if (menuId == null) throw new IllegalArgumentException("menuId required");
        if (action == null) throw new IllegalArgumentException("action required");
    }
}
