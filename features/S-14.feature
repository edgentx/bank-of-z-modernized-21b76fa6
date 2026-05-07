Feature: Implement CompleteTransferCmd on Transfer (transaction-processing)

  Feature: CompleteTransferCmd

    Scenario: Successfully execute CompleteTransferCmd
      Given a valid Transfer aggregate
      And a valid transferReference is provided
      When the CompleteTransferCmd command is executed
      Then a transfer.completed event is emitted

    Scenario: CompleteTransferCmd rejected — Source and destination accounts cannot be the same.
      Given a Transfer aggregate that violates: Source and destination accounts cannot be the same.
      When the CompleteTransferCmd command is executed
      Then the command is rejected with a domain error

    Scenario: CompleteTransferCmd rejected — Transfer amount must not exceed the available balance of the source account.
      Given a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.
      When the CompleteTransferCmd command is executed
      Then the command is rejected with a domain error

    Scenario: CompleteTransferCmd rejected — A transfer must succeed or fail atomically for both accounts involved.
      Given a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.
      When the CompleteTransferCmd command is executed
      Then the command is rejected with a domain error
