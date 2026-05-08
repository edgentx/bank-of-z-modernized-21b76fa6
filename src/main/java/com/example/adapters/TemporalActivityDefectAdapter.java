package com.example.adapters;

import com.example.domain.vforce360.ReportDefectCmd;
import com.example.domain.vforce360.VForce360Aggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Implementation for reporting defects.
 * This acts as the bridge between the Temporal workflow orchestration and the Domain Logic.
 */
@Component
public class TemporalActivityDefectAdapter {

    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    public TemporalActivityDefectAdapter(GitHubPort gitHubPort, SlackPort slackPort) {
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    /**
     * Executes the Report Defect workflow logic.
     * Corresponds to the 'report_defect' activity.
     */
    public void executeReportDefect(String defectId, String title, String description) {
        // Instantiate the aggregate with the required ports
        VForce360Aggregate aggregate = new VForce360Aggregate(defectId, gitHubPort, slackPort);
        
        // Create command
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, description);
        
        // Execute domain logic
        aggregate.execute(cmd);
    }
}
