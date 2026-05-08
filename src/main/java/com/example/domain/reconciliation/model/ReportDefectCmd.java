package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.util.UUID;

/**
 * Command to report a defect.
 * Context: S-FB-1
 */
public record ReportDefectCmd(
        String batchId,
        String reason,
        String githubIssueUrl
) implements Command {
    public ReportDefectCmd {
        if (batchId == null || batchId.isBlank()) throw new IllegalArgumentException("batchId required");
        if (githubIssueUrl == null || githubIssueUrl.isBlank()) throw new IllegalArgumentException("githubIssueUrl required");
    }

    public static ReportDefectCmd withRandomId(String batchId, String reason, String url) {
        return new ReportDefectCmd(batchId, reason, url);
    }
}
