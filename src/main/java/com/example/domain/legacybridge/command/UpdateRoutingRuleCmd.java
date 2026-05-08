package com.example.domain.legacybridge.command;

import com.example.domain.shared.Command;

import java.time.Instant;

/**
 * Command to update the routing rule configuration.
 * Fixes critical feedback: Added newRuleVersion parameter.
 */
public record UpdateRoutingRuleCmd(
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int newRuleVersion
) implements Command {}
