package com.example.api.legacybridge.dto;

import com.example.domain.legacybridge.model.DataSyncCheckpoint;

public record CheckpointResponse(
    String checkpointId,
    long currentOffset,
    int version
) {
  public static CheckpointResponse from(DataSyncCheckpoint checkpoint) {
    return new CheckpointResponse(
        checkpoint.id(),
        checkpoint.getCurrentOffset(),
        checkpoint.getVersion());
  }
}
