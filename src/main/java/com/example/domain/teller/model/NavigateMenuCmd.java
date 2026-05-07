package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String tellerId,
    String currentContext
) implements Command {}
