package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to navigate the teller interface to a new menu or screen.
 * Mirrors legacy 3270 data flow (AID keys + screen cursor).
 */
public record NavigateMenuCmd(
        String sessionId,
        String targetMenuId,
        String action, // e.g. ENTER, PF3, PF12, CLEAR
        Instant timestamp
) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (targetMenuId == null || targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
        if (timestamp == null) throw new IllegalArgumentException("timestamp required");
    }
}
