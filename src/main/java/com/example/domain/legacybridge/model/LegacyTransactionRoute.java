package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;
import java.util.Map;

/**
 * Aggregate responsible for determining the target system (Legacy vs Modern)
 * for incoming transactions based on feature flags and versioned routing rules.
 * Consolidated into domain.legacybridge.model per S-23 requirements.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private int currentRulesVersion;
    private boolean isDualWriteEnabled; // Flag for dual-processing simulation

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        // Default state for testing purposes
        this.currentRulesVersion = 1;
        this.isDualWriteEnabled = false;
    }

    @Override
    public String id() {
        return routeId;
    }

    /**
     * Allows tests to set up specific invariant violation states.
     * In production, this would be loaded from event history.
     */
    public void configure(int rulesVersion, boolean dualWriteEnabled) {
        this.currentRulesVersion = rulesVersion;
        this.isDualWriteEnabled = dualWriteEnabled;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant Check 1: Routing rules must be versioned to allow safe rollback.
        // Scenario: The command attempts to apply a version that doesn't match the current supported version.
        if (cmd.targetRulesVersion() <= 0) {
             throw new IllegalArgumentException("Target rules version must be positive");
        }

        if (cmd.targetRulesVersion() > this.currentRulesVersion) {
            throw new IllegalStateException(
                "Cannot evaluate routing: Requested version " + cmd.targetRulesVersion() +
                " is newer than current aggregate version " + this.currentRulesVersion +
                ". Routing rules must be versioned to allow safe rollback."
            );
        }

        // Invariant Check 2: A transaction must route to exactly one backend system (modern or legacy).
        // Scenario: System is configured for dual-processing (feature flag violation).
        if (this.isDualWriteEnabled) {
            throw new IllegalStateException(
                "Routing evaluation failed: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing."
            );
        }

        // Logic to determine target system (Mocked for domain layer)
        String targetSystem = determineTargetSystem(cmd.transactionType());

        var event = new RoutingEvaluatedEvent(
            this.routeId,
            cmd.transactionType(),
            targetSystem,
            cmd.payload(),
            cmd.targetRulesVersion(),
            java.time.Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTargetSystem(String transactionType) {
        // Simplified routing logic for the domain layer implementation
        return "LEGACY";
    }
}