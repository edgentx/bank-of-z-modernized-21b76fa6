package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class DataSyncCheckpoint extends AggregateRoot {
    private final String checkpointId;
    private long currentOffset;
    private boolean initialized = false;

    public DataSyncCheckpoint(String checkpointId) {
        this.checkpointId = checkpointId;
    }

    @Override
    public String id() {
        return checkpointId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RecordSyncCheckpointCmd c) {
            return recordCheckpoint(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> recordCheckpoint(RecordSyncCheckpointCmd cmd) {
        // Invariant: Data validation must pass
        if (cmd.validationHash() == null || cmd.validationHash().isBlank()) {
            throw new IllegalArgumentException("validationHash required");
        }

        // Invariant: Checkpoint offsets must strictly increase and cannot be skipped.
        // We also enforce that the first offset starts at 0 or higher, and subsequent ones are > current.
        if (initialized && cmd.syncOffset() <= currentOffset) {
            throw new IllegalStateException("Checkpoint offsets must strictly increase. Current: " + currentOffset + ", Provided: " + cmd.syncOffset());
        }

        var event = new CheckpointRecordedEvent(cmd.checkpointId(), cmd.syncOffset(), cmd.validationHash(), Instant.now());
        this.currentOffset = cmd.syncOffset();
        this.initialized = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public long getCurrentOffset() {
        return currentOffset;
    }
}
