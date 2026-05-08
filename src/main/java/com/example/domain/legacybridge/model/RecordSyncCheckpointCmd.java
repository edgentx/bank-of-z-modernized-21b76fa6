package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record RecordSyncCheckpointCmd(String checkpointId, long syncOffset, String validationHash) implements Command {
    public RecordSyncCheckpointCmd {
        Objects.requireNonNull(checkpointId, "checkpointId required");
    }
}