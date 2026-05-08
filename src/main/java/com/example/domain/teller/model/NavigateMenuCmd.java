package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    boolean isAuthenticated,
    boolean isSessionActive,
    String currentState,
    String requestedContext
) implements Command {}
