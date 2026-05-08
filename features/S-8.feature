Feature: Implement GenerateStatementCmd on Statement (account-management)

  Scenario: Successfully execute GenerateStatementCmd
    Given a valid Statement aggregate
    And a valid accountNumber is provided
    And a valid periodEnd is provided
    When the GenerateStatementCmd command is executed
    Then a statement.generated event is emitted

  Scenario: GenerateStatementCmd rejected — A statement must be generated for a closed period and cannot be altered retroactively.
    Given a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.
    When the GenerateStatementCmd command is executed
    Then the command is rejected with a domain error

  Scenario: GenerateStatementCmd rejected — Statement opening balance must exactly match the closing balance of the previous statement.
    Given a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.
    When the GenerateStatementCmd command is executed
    Then the command is rejected with a domain error