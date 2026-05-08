package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}