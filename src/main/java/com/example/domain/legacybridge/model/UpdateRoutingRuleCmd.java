package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to update an existing routing rule.
 * Used to shift traffic from Legacy to Modern systems.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int targetVersion
) implements Command {}
