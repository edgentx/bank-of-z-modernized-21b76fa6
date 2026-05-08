package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
    }
}
