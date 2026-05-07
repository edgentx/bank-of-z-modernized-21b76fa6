package com.example.domain.teller.model;

import com.example.domain.shared.Command;

public record NavigateMenuCmd(
        String sessionId,
        String targetMenuId,
        String action
) implements Command {}
