Feature: Implement VerifyDataParityCmd on DataSyncCheckpoint (legacy-bridge)

  Feature: VerifyDataParityCmd

    Scenario: Successfully execute VerifyDataParityCmd
      Given a valid DataSyncCheckpoint aggregate
      And a valid entityType is provided
      And a valid dateRange is provided
      When the VerifyDataParityCmd command is executed
      Then a parity.verified event is emitted

    Scenario: VerifyDataParityCmd rejected — Checkpoint offsets must strictly increase and cannot be skipped.
      Given a DataSyncCheckpoint aggregate that violates: Checkpoint offsets must strictly increase and cannot be skipped.
      When the VerifyDataParityCmd command is executed
      Then the command is rejected with a domain error

    Scenario: VerifyDataParityCmd rejected — Data validation must pass before a checkpoint is committed.
      Given a DataSyncCheckpoint aggregate that violates: Data validation must pass before a checkpoint is committed.
      When the VerifyDataParityCmd command is executed
      Then the command is rejected with a domain error
