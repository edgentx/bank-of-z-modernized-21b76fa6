package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to update a routing rule configuration.
 * Used to shift traffic or modify versioning rules.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate
) implements Command {}
