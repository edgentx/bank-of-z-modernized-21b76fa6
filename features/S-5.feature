Feature: Implement OpenAccountCmd on Account (account-management)

  Scenario: Successfully execute OpenAccountCmd
    Given a valid Account aggregate
    And a valid customerId is provided
    And a valid accountType is provided
    And a valid initialDeposit is provided
    And a valid sortCode is provided
    When the OpenAccountCmd command is executed
    Then a account.opened event is emitted

  Scenario: OpenAccountCmd rejected - Account balance cannot drop below the minimum required balance for its specific account type.
    Given a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.
    When the OpenAccountCmd command is executed
    Then the command is rejected with a domain error

  Scenario: OpenAccountCmd rejected - An account must be in an Active status to process withdrawals or transfers.
    Given a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.
    When the OpenAccountCmd command is executed
    Then the command is rejected with a domain error

  Scenario: OpenAccountCmd rejected - Account numbers must be uniquely generated and immutable.
    Given a Account aggregate that violates: Account numbers must be uniquely generated and immutable.
    When the OpenAccountCmd command is executed
    Then the command is rejected with a domain error
