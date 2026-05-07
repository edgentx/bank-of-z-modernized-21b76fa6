package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record NavigateMenuCmd(String sessionId, String menuId, String action, Instant occurredAt) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
