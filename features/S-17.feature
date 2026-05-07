Feature: Implement ForceBalanceCmd on ReconciliationBatch (transaction-processing)
  Feature: ForceBalanceCmd

    Scenario: Successfully execute ForceBalanceCmd
      Given a valid ReconciliationBatch aggregate
      And a valid batchId is provided
      And a valid operatorId is provided
      And a valid justification is provided
      When the ForceBalanceCmd command is executed
      Then a reconciliation.balanced event is emitted

    Scenario: ForceBalanceCmd rejected — A reconciliation batch cannot be executed if a previous batch is still pending.
      Given a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.
      When the ForceBalanceCmd command is executed
      Then the command is rejected with a domain error

    Scenario: ForceBalanceCmd rejected — All transaction entries must be accounted for during the reconciliation period.
      Given a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.
      When the ForceBalanceCmd command is executed
      Then the command is rejected with a domain error
