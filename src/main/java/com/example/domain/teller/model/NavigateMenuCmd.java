package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the Teller UI to a specific menu.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}
