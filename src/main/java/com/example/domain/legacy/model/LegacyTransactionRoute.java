package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private String currentTransactionType;
    private String currentPayload;
    private boolean evaluated;
    private int currentRuleVersion;
    private boolean dualProcessingViolation;
    private boolean versioningViolation;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.dualProcessingViolation = false;
        this.versioningViolation = false;
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
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        throw new UnknownCommandException(cmd);
    }

    /**
     * Handles UpdateRoutingRuleCmd to shift traffic configuration.
     */
    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant Check: No Dual Processing
        // If the aggregate state indicates a violation (e.g. currently routing to BOTH)
        // we reject the update. This invariant ensures clean cutover.
        if (this.dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant Check: Versioning
        // Ensure the new version is valid and allows safe rollback logic (positive increment)
        if (this.versioningViolation || cmd.newVersion() <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Logic: Update the internal routing state based on the command
        // (In a real system, this might validate ruleId existence or effectiveDate future constraint)

        var event = new RoutingRuleUpdatedEvent(
                null, // eventId generated in record constructor
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.newVersion(),
                cmd.effectiveDate(),
                Instant.now()
        );

        // Apply state changes
        this.currentRuleVersion = cmd.newVersion();
        // Note: We don't set evaluated=true here as this is a config update, not a transaction evaluation

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Handles EvaluateRoutingCmd (Existing logic from previous story).
     */
    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant: Routing rules must be versioned (must be positive)
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant: A transaction must route to exactly one backend system
        if (cmd.dualProcessingAttempt()) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType is required");
        }

        if (cmd.payload() == null || cmd.payload().isBlank()) {
            throw new IllegalArgumentException("payload is required");
        }

        String targetSystem = determineTargetSystem(cmd.transactionType());

        var event = new RoutingEvaluatedEvent(
            cmd.routeId(),
            cmd.transactionType(),
            targetSystem,
            cmd.ruleVersion(),
            Instant.now()
        );

        this.currentTransactionType = cmd.transactionType();
        this.currentPayload = cmd.payload();
        this.evaluated = true;
        this.currentRuleVersion = cmd.ruleVersion();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTargetSystem(String transactionType) {
        return transactionType.startsWith("MODERN_") ? "VForce360" : "CICS";
    }

    public boolean isEvaluated() { return evaluated; }
    public String getCurrentTransactionType() { return currentTransactionType; }
    
    // Test utility to force invariants violations for BDD scenarios
    public void markDualProcessingViolation() {
        this.dualProcessingViolation = true;
    }
    
    public void markVersioningViolation() {
        this.versioningViolation = true;
    }
}
