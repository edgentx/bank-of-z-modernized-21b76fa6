Feature: Implement ExportStatementCmd on Statement (account-management)

  Scenario: Successfully execute ExportStatementCmd
    Given a valid Statement aggregate
    And a valid statementId is provided
    And a valid format is provided
    When the ExportStatementCmd command is executed
    Then a statement.exported event is emitted

  Scenario: ExportStatementCmd rejected — A statement must be generated for a closed period and cannot be altered retroactively.
    Given a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.
    When the ExportStatementCmd command is executed
    Then the command is rejected with a domain error

  Scenario: ExportStatementCmd rejected — Statement opening balance must exactly match the closing balance of the previous statement.
    Given a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.
    When the ExportStatementCmd command is executed
    Then the command is rejected with a domain error