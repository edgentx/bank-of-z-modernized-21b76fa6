Feature: Implement InitiateTransferCmd on Transfer (transaction-processing)

  Feature: InitiateTransferCmd

    Scenario: Successfully execute InitiateTransferCmd
      Given a valid Transfer aggregate
      And a valid fromAccount is provided
      And a valid toAccount is provided
      And a valid amount is provided
      When the InitiateTransferCmd command is executed
      Then a transfer.initiated event is emitted

    Scenario: InitiateTransferCmd rejected — Source and destination accounts cannot be the same.
      Given a Transfer aggregate that violates: Source and destination accounts cannot be the same.
      When the InitiateTransferCmd command is executed
      Then the command is rejected with a domain error

    Scenario: InitiateTransferCmd rejected — Transfer amount must not exceed the available balance of the source account.
      Given a Transfer aggregate that violates: Transfer amount must not exceed the available balance of the source account.
      When the InitiateTransferCmd command is executed
      Then the command is rejected with a domain error

    Scenario: InitiateTransferCmd rejected — A transfer must succeed or fail atomically for both accounts involved.
      Given a Transfer aggregate that violates: A transfer must succeed or fail atomically for both accounts involved.
      When the InitiateTransferCmd command is executed
      Then the command is rejected with a domain error
