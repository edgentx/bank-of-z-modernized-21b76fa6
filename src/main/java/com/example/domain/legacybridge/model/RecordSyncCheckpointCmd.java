package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

public record RecordSyncCheckpointCmd(
    String aggregateId,
    long syncOffset,
    String validationHash
) implements Command {}
