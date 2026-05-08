package com.example.domain.vw454;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (triggered via Temporal, etc).
 * Corresponds to S-FB-1 trigger.
 */
public record ReportDefectCmd(
    String defectId,
    String summary,
    String repoUrl,
    String severity
) implements Command {}
