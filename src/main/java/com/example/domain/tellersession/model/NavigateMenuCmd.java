package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements Command {}
