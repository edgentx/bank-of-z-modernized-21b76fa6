package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Record pattern (Java 21+) for immutable data carrier.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}
