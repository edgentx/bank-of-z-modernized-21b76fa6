package com.example.domain.legacybridge.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger a comparison job between legacy and modern data stores.
 * S-26: VerifyDataParityCmd on DataSyncCheckpoint.
 */
public record VerifyDataParityCmd(
        String checkpointId,
        String entityType,
        long syncOffset,
        String dateRange,
        String validationHash
) implements Command {}
