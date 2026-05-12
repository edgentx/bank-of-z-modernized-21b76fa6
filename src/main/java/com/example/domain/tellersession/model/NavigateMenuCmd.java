package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
  public NavigateMenuCmd {
    if (sessionId == null || sessionId.isBlank()) {
      throw new IllegalArgumentException("Session ID cannot be null or blank");
    }
    if (menuId == null || menuId.isBlank()) {
      throw new IllegalArgumentException("Menu ID cannot be null or blank");
    }
    if (action == null || action.isBlank()) {
      throw new IllegalArgumentException("Action cannot be null or blank");
    }
  }
}
