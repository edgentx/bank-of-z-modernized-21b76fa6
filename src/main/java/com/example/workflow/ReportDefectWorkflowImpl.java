package com.example.workflow;

import com.example.activities.DefectReportingActivitiesImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workflow Implementation.
 * Orchestrates the activities: Create GitHub Issue -> Notify Slack.
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectWorkflowImpl.class);

    // Workflow stub allows calling Activities defined in the interface
    private final DefectReportingActivitiesImpl activities = Workflow.newActivityStub(DefectReportingActivitiesImpl.class);

    @Override
    public void reportDefect(String defectId, String description) {
        log.info("Starting defect report workflow for ID: {}", defectId);
        
        // Step 1: Create Issue in GitHub
        String url = activities.createGitHubIssue(description);
        
        // Step 2: Notify Slack with the URL
        String body = "Defect Reported: " + description + "\nGitHub Issue: " + url;
        activities.notifySlack(body);
        
        log.info("Defect report workflow completed for ID: {}", defectId);
    }
}