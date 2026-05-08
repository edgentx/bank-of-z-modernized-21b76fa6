package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate responsible for determining the target system (Modern vs Legacy)
 * for incoming transactions based on feature flags and versioned routing rules.
 * S-23: Implement EvaluateRoutingCmd.
 */
public class LegacyTransactionRoute extends AggregateRoot {

  private final String routeId;
  private int currentRulesVersion = 0;
  private String lastTargetSystem;

  // Constants for Invariants
  private static final int MIN_RULES_VERSION = 1;

  public LegacyTransactionRoute(String routeId) {
    this.routeId = routeId;
  }

  @Override
  public String id() {
    return routeId;
  }

  @Override
  public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof EvaluateRoutingCmd c) {
      return evaluateRouting(c);
    }
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
    // Invariant: Routing rules must be versioned to allow safe rollback.
    // Check 1: Version must be positive (versioning requirement).
    if (cmd.targetRulesVersion() < MIN_RULES_VERSION) {
      throw new IllegalArgumentException(
          "Routing rules must be versioned to allow safe rollback. Invalid version: " + cmd.targetRulesVersion()
      );
    }

    // Invariant: A transaction must route to exactly one backend system (modern or legacy).
    // Check 2: Payload must not suggest ambiguity. (Simulated logic: payload must not contain 'dual-route' flag).
    boolean isAmbiguous = Boolean.TRUE.equals(cmd.payload().get("dual-route"));
    if (isAmbiguous) {
      throw new IllegalArgumentException(
          "A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing."
      );
    }

    // Determine Target System (Simplified Logic for S-23)
    String targetSystem;
    // Example logic: if version is odd -> Legacy, even -> Modern (arbitrary rule for testing)
    // Or check payload for 'target'. Let's assume cmd provides the decision or we derive it.
    // For this implementation, we'll derive it from the transactionType or default to 'LEGACY'.
    if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
       throw new IllegalArgumentException("transactionType is required");
    }

    // Simple business rule for the story:
    // If transaction type is 'MODERN_PAYMENT', route to MODERN. Else LEGACY.
    targetSystem = "MODERN_PAYMENT".equals(cmd.transactionType()) ? "MODERN" : "LEGACY";

    // Emit Event
    var event = new RoutingEvaluatedEvent(
        this.routeId,
        cmd.transactionType(),
        targetSystem,
        cmd.targetRulesVersion(),
        Instant.now()
    );

    // Apply state changes (hygiene)
    this.lastTargetSystem = targetSystem;
    this.currentRulesVersion = cmd.targetRulesVersion();

    addEvent(event);
    incrementVersion();

    return List.of(event);
  }

  // Getters for verification
  public int getCurrentRulesVersion() { return currentRulesVersion; }
  public String getLastTargetSystem() { return lastTargetSystem; }
}
