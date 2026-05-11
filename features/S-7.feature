Feature: Implement CloseAccountCmd on Account (account-management)

  Feature: CloseAccountCmd

    Scenario: Successfully execute CloseAccountCmd
      Given a valid Account aggregate
      And a valid accountNumber is provided
      When the CloseAccountCmd command is executed
      Then a account.closed event is emitted

    Scenario: CloseAccountCmd rejected — Account balance cannot drop below the minimum required balance for its specific account type.
      Given a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.
      When the CloseAccountCmd command is executed
      Then the command is rejected with a domain error

    Scenario: CloseAccountCmd rejected — An account must be in an Active status to process withdrawals or transfers.
      Given a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.
      When the CloseAccountCmd command is executed
      Then the command is rejected with a domain error

    Scenario: CloseAccountCmd rejected — Account numbers must be uniquely generated and immutable.
      Given a Account aggregate that violates: Account numbers must be uniquely generated and immutable.
      When the CloseAccountCmd command is executed
      Then the command is rejected with a domain error
