package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the TellerSession to a specific menu/action.
 * Emulates legacy 3270 screen navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}
