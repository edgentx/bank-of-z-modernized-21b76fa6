package com.example.domain.routing.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update a specific routing rule for the Legacy Transaction Route.
 * Part of Story S-24: Legacy Bridge Modernization.
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate
) implements Command {
}