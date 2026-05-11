Feature: Implement UpdateRoutingRuleCmd on LegacyTransactionRoute (legacy-bridge)

  Feature: UpdateRoutingRuleCmd

    Scenario: Successfully execute UpdateRoutingRuleCmd
      Given a valid LegacyTransactionRoute aggregate
      And a valid ruleId is provided
      And a valid newTarget is provided
      And a valid effectiveDate is provided
      When the UpdateRoutingRuleCmd command is executed
      Then a routing.updated event is emitted

    Scenario: UpdateRoutingRuleCmd rejected — A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.
      Given a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.
      When the UpdateRoutingRuleCmd command is executed
      Then the command is rejected with a domain error

    Scenario: UpdateRoutingRuleCmd rejected — Routing rules must be versioned to allow safe rollback.
      Given a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.
      When the UpdateRoutingRuleCmd command is executed
      Then the command is rejected with a domain error
