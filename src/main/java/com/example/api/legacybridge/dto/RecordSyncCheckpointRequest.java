package com.example.api.legacybridge.dto;

import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record RecordSyncCheckpointRequest(
    @NotBlank String checkpointId,
    @PositiveOrZero long syncOffset,
    @NotBlank String validationHash
) {
  public RecordSyncCheckpointCmd toCommand() {
    return new RecordSyncCheckpointCmd(checkpointId, syncOffset, validationHash);
  }
}
