Feature: Implement ReverseTransactionCmd on Transaction (transaction-processing)

  Scenario: Successfully execute ReverseTransactionCmd
    Given a valid Transaction aggregate
    And a valid originalTransactionId is provided
    When the ReverseTransactionCmd command is executed
    Then a transaction.reversed event is emitted

  Scenario: ReverseTransactionCmd rejected — Transaction amounts must be greater than zero.
    Given a Transaction aggregate that violates: Transaction amounts must be greater than zero.
    When the ReverseTransactionCmd command is executed with zero amount
    Then the command is rejected with a domain error

  Scenario: ReverseTransactionCmd rejected — Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.
    Given a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.
    When the ReverseTransactionCmd command is executed on posted transaction
    Then the command is rejected with a domain error

  Scenario: ReverseTransactionCmd rejected — A transaction must result in a valid account balance (enforced via aggregate validation).
    Given a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).
    When the ReverseTransactionCmd command is executed with invalid balance context
    Then the command is rejected with a domain error
