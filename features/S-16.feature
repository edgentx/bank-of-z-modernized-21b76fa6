Feature: Implement StartReconciliationCmd on ReconciliationBatch (transaction-processing)

  Scenario: Successfully execute StartReconciliationCmd
    Given a valid ReconciliationBatch aggregate
    And a valid batchWindow is provided
    When the StartReconciliationCmd command is executed
    Then a reconciliation.started event is emitted

  Scenario: StartReconciliationCmd rejected — A reconciliation batch cannot be executed if a previous batch is still pending.
    Given a ReconciliationBatch aggregate that violates: A reconciliation batch cannot be executed if a previous batch is still pending.
    When the StartReconciliationCmd command is executed
    Then the command is rejected with a domain error

  Scenario: StartReconciliationCmd rejected — All transaction entries must be accounted for during the reconciliation period.
    Given a ReconciliationBatch aggregate that violates: All transaction entries must be accounted for during the reconciliation period.
    When the StartReconciliationCmd command is executed
    Then the command is rejected with a domain error