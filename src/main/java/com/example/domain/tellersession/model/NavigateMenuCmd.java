package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
    String sessionId,
    String currentMenuId,
    String currentContext,
    String targetMenuId,
    String targetContext,
    String action
) implements Command {}
