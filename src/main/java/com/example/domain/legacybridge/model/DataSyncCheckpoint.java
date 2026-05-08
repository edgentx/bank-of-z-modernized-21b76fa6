package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class DataSyncCheckpoint extends AggregateRoot {

    private final String id;
    private long lastSyncOffset = -1;
    private boolean hashValidated = false;

    public DataSyncCheckpoint(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof RecordSyncCheckpointCmd c) {
            return recordCheckpoint(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> recordCheckpoint(RecordSyncCheckpointCmd cmd) {
        // Invariant: Checkpoint offsets must strictly increase
        if (cmd.syncOffset() <= lastSyncOffset) {
            throw new IllegalStateException(
                String.format("Checkpoint offset %d must be strictly greater than current %d",
                    cmd.syncOffset(), lastSyncOffset)
            );
        }

        // Invariant: Data validation must pass before a checkpoint is committed
        if (cmd.validationHash() == null || cmd.validationHash().isBlank()) {
            throw new IllegalArgumentException("Validation hash must be provided to commit checkpoint");
        }

        var event = new SyncCheckpointRecordedEvent(cmd.aggregateId(), cmd.syncOffset(), cmd.validationHash(), Instant.now());

        // Apply state changes
        this.lastSyncOffset = cmd.syncOffset();
        this.hashValidated = true;

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    public long getLastSyncOffset() {
        return lastSyncOffset;
    }

    public boolean isHashValidated() {
        return hashValidated;
    }
}
