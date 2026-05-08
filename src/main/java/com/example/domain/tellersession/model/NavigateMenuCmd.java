package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.UUID;

public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}
