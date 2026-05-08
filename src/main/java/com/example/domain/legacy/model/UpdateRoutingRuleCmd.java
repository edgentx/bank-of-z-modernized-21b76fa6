package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to update a routing rule.
 */
public record UpdateRoutingRuleCmd(
        String routeId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int newVersion
) implements Command {}
