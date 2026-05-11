package com.example.adapters;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Worker Adapter for Defect Reporting.
 * S-FB-1: Handles the workflow logic triggered by the defect report.
 */
public class DefectReportTemporalAdapter {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectReportTemporalAdapter(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    /**
     * Exposed as a Temporal Activity.
     * 1. Triggers report_defect via temporal-worker exec.
     * 2. Verifies/Ensures Slack body contains GitHub issue link.
     */
    @ActivityInterface
    public interface ReportDefectActivity {
        @ActivityMethod
        String executeReportDefect(String conversationId, String defectId);
    }

    public String executeReportDefect(String conversationId, String defectId) {
        // 1. Create Domain Command
        ReportDefectCmd cmd = new ReportDefectCmd(conversationId, defectId);
        VForce360Aggregate aggregate = new VForce360Aggregate(conversationId);
        
        // 2. Execute Aggregate Logic (Simulated)
        aggregate.execute(cmd);

        // 3. Call GitHub (Simulated)
        String ghUrl = gitHubPort.createIssue("Defect: " + defectId, "Reported via VForce360");
        
        // Update Aggregate state with the resulting URL (simulation of event sourcing update)
        aggregate.setGithubIssueUrl(ghUrl);

        // 4. Construct Slack Body
        // S-FB-1 Requirement: Slack body includes GitHub issue: <url>
        String slackBody = "Defect Report created for: " + defectId + "\n" +
                           "GitHub issue: " + ghUrl;

        // 5. Send Slack Message
        slackPort.sendSlackMessage("#vforce360-issues", slackBody);

        return ghUrl;
    }
}
