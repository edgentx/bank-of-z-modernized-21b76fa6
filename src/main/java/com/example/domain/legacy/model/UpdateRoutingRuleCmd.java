package com.example.domain.legacy.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update routing rules for the Legacy Transaction Route.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate
) implements Command {
}