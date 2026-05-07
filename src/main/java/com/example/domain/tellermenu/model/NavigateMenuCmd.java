package com.example.domain.tellermenu.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}
