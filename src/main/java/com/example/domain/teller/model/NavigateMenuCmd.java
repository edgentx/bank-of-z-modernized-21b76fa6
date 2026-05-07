package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the Teller UI to a specific menu/screen.
 * Part of Story S-19.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        // MenuId and Action can be null depending on context, but typically provided.
    }
}