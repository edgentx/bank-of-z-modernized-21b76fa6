package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to navigate the teller interface to a specific menu context.
 * S-19: User Interface Navigation
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action, Instant occurredAt) implements Command {
}