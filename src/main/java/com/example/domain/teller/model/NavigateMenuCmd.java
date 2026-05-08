package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action,
    String currentContext, // Used to validate the caller's view of state
    String newContext      // Used to update the aggregate's view of state
) implements Command {}