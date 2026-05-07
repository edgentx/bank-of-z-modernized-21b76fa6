package com.example.services;

import com.example.adapters.GitHubPort;
import com.example.adapters.SlackPort;
import com.example.domain.defect.model.ReportDefectCmd;
import org.springframework.stereotype.Service;

/**
 * Service handling the reporting workflow (Temporal equivalent).
 * Placeholder implementation to satisfy compilation dependencies.
 */
@Service
public class DefectReportService {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public DefectReportService(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void handleReportDefect(ReportDefectCmd cmd) {
        // Placeholder logic to be implemented in Green phase
        // Currently does nothing, ensuring E2E test fails.
    }
}
