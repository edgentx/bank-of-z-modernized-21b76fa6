Feature: Implement EvaluateRoutingCmd on LegacyTransactionRoute (legacy-bridge)

  Scenario: Successfully execute EvaluateRoutingCmd
    Given a valid LegacyTransactionRoute aggregate
    And a valid transactionType is provided
    And a valid payload is provided
    When the EvaluateRoutingCmd command is executed
    Then a routing.evaluated event is emitted

  Scenario: EvaluateRoutingCmd rejected — A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.
    Given a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.
    When the EvaluateRoutingCmd command is executed
    Then the command is rejected with a domain error

  Scenario: EvaluateRoutingCmd rejected — Routing rules must be versioned to allow safe rollback.
    Given a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.
    When the EvaluateRoutingCmd command is executed
    Then the command is rejected with a domain error
