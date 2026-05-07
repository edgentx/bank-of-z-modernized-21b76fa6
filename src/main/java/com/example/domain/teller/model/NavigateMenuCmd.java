package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or action.
 */
public record NavigateMenuCmd(
        String sessionId,
        String targetMenuId,
        String action,
        String context
) implements Command {

    public boolean requiresContext() {
        // Logic to determine if the target menu requires specific operational context.
        // For example, navigating to "DEPOSIT_DETAIL" might require a transaction ID.
        return "DEPOSIT_DETAIL".equals(targetMenuId) || "WITHDRAWAL_DETAIL".equals(targetMenuId);
    }
}
