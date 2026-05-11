Feature: UpdateAccountStatusCmd

  Scenario: Successfully execute UpdateAccountStatusCmd
    Given a valid Account aggregate
    And a valid accountNumber is provided
    And a valid newStatus is provided
    When the UpdateAccountStatusCmd command is executed
    Then a account.status.updated event is emitted

  Scenario: UpdateAccountStatusCmd rejected — Account balance cannot drop below the minimum required balance for its specific account type.
    Given a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.
    When the UpdateAccountStatusCmd command is executed
    Then the command is rejected with a domain error

  Scenario: UpdateAccountStatusCmd rejected — An account must be in an Active status to process withdrawals or transfers.
    Given a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.
    When the UpdateAccountStatusCmd command is executed
    Then the command is rejected with a domain error

  Scenario: UpdateAccountStatusCmd rejected — Account numbers must be uniquely generated and immutable.
    Given a Account aggregate that violates: Account numbers must be uniquely generated and immutable.
    When the UpdateAccountStatusCmd command is executed
    Then the command is rejected with a domain error
