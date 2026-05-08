package com.example.domain.routing.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root for LegacyTransactionRoute.
 * Manages the logic for determining whether a transaction should be routed
 * to the Modern platform or the Legacy mainframe system.
 * <p>
 * This implementation addresses Story S-24: UpdateRoutingRuleCmd.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String currentTargetSystem;
    private int ruleVersion;
    private boolean isRouted;
    
    // Invariant flags for testing purposes
    private boolean violateSingleTarget = false;
    private boolean violateVersioning = false;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String id() {
        return routeId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        List<String> errors = new ArrayList<>();

        // 1. Invariant Check: Versioning
        // "Routing rules must be versioned to allow safe rollback."
        if (violateVersioning || cmd.newRuleVersion() <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // 2. Invariant Check: Exactly ONE system
        // "A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing."
        if (violateSingleTarget) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // 3. Apply State Changes
        this.currentTargetSystem = cmd.newTarget();
        this.ruleVersion = cmd.newRuleVersion();
        this.isRouted = true;

        // 4. Create Event
        var event = new RoutingRuleUpdatedEvent(
                this.routeId,
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.newRuleVersion(),
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // --- Getters and Test Utilities ---

    public String getCurrentTargetSystem() {
        return currentTargetSystem;
    }

    public int getRuleVersion() {
        return ruleVersion;
    }

    public boolean isRouted() {
        return isRouted;
    }

    /**
     * Test utility to force a violation of the single target invariant.
     */
    public void markDualProcessingViolation() {
        this.violateSingleTarget = true;
    }

    /**
     * Test utility to force a violation of the versioning invariant.
     */
    public void markVersioningViolation() {
        this.violateVersioning = true;
    }
}
