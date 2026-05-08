Feature: Implement RecordSyncCheckpointCmd on DataSyncCheckpoint (legacy-bridge)
  Feature: RecordSyncCheckpointCmd

    Scenario: Successfully execute RecordSyncCheckpointCmd
      Given a valid DataSyncCheckpoint aggregate
      And a valid syncOffset is provided
      And a valid validationHash is provided
      When the RecordSyncCheckpointCmd command is executed
      Then a checkpoint.recorded event is emitted

    Scenario: RecordSyncCheckpointCmd rejected — Checkpoint offsets must strictly increase and cannot be skipped.
      Given a DataSyncCheckpoint aggregate that violates: Checkpoint offsets must strictly increase and cannot be skipped.
      When the RecordSyncCheckpointCmd command is executed
      Then the command is rejected with a domain error

    Scenario: RecordSyncCheckpointCmd rejected — Data validation must pass before a checkpoint is committed.
      Given a DataSyncCheckpoint aggregate that violates: Data validation must pass before a checkpoint is committed.
      When the RecordSyncCheckpointCmd command is executed
      Then the command is rejected with a domain error
