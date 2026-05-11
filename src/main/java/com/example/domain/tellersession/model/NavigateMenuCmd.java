package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to navigate the teller to a specific menu or screen.
 * Emulates legacy menu navigation behavior.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    Instant timestamp
) implements Command {}
