package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Part of User Interface Navigation (S-19).
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action
) implements Command {}
