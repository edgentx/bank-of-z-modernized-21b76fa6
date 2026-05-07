Feature: Implement PostWithdrawalCmd on Transaction (transaction-processing)
  Feature: PostWithdrawalCmd

    Scenario: Successfully execute PostWithdrawalCmd
      Given a valid Transaction aggregate
      And a valid accountNumber is provided
      And a valid amount is provided
      And a valid currency is provided
      When the PostWithdrawalCmd command is executed
      Then a withdrawal.posted event is emitted

    Scenario: PostWithdrawalCmd rejected — Transaction amounts must be greater than zero.
      Given a Transaction aggregate that violates: Transaction amounts must be greater than zero.
      When the PostWithdrawalCmd command is executed
      Then the command is rejected with a domain error

    Scenario: PostWithdrawalCmd rejected — Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.
      Given a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.
      When the PostWithdrawalCmd command is executed
      Then the command is rejected with a domain error

    Scenario: PostWithdrawalCmd rejected — A transaction must result in a valid account balance (enforced via aggregate validation).
      Given a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).
      When the PostWithdrawalCmd command is executed
      Then the command is rejected with a domain error