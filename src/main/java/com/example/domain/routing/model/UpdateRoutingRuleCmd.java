package com.example.domain.routing.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update a specific routing rule.
 * Used to shift traffic from Legacy to Modern systems.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate
) implements Command {}
