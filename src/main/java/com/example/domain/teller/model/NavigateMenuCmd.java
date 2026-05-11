package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the Teller UI to a specific menu context.
 * S-19: user-interface-navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String tellerId,
    long lastActivityTimestamp
) implements Command {

  public NavigateMenuCmd {
    Objects.requireNonNull(sessionId, "sessionId cannot be null");
    Objects.requireNonNull(menuId, "menuId cannot be null");
    Objects.requireNonNull(action, "action cannot be null");
    // tellerId and lastActivityTimestamp are optional or defaulted, but null checks prevent NPEs in logic
  }
}
