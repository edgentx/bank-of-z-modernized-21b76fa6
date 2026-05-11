package com.example.domain.legacy.command;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update a routing rule for a Legacy Transaction Route.
 * Used to shift traffic from legacy to modern systems.
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int newVersion
) implements Command {
}
