package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the teller UI to a specific menu context.
 * Legacy 3270 navigation emulation (S-19).
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
  public NavigateMenuCmd {
    Objects.requireNonNull(sessionId, "sessionId cannot be null");
    Objects.requireNonNull(menuId, "menuId cannot be null");
    Objects.requireNonNull(action, "action cannot be null");
  }
}
