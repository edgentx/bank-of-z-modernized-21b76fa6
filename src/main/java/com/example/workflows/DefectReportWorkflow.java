package com.example.workflows;

import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Temporal Workflow for reporting a defect.
 * Orchestrates creating the issue and notifying the channel.
 */
@WorkflowInterface
public interface DefectReportWorkflow {

    @WorkflowMethod
    void reportDefect(String title, String body);

    /**
     * Workflow implementation.
     * In a real setup, this might be a separate class file, but inner classes are often used for Temporal samples.
     */
    class WorkflowImpl implements DefectReportWorkflow {
        private static final Logger log = LoggerFactory.getLogger(WorkflowImpl.class);

        // Activity stubs
        private final DefectReportActivities activities = Workflow.newActivityStub(
            DefectReportActivities.class,
            // Options: e.g., timeouts, retries
            io.temporal.activity.ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(10))
                .build()
        );

        @Override
        public void reportDefect(String title, String body) {
            log.info("Starting defect report workflow for: {}", title);

            // 1. Create GitHub Issue
            String issueUrl = activities.createGitHubIssue(title, body);
            log.info("Created issue: {}", issueUrl);

            // 2. Notify Slack (This is where VW-454 applies)
            activities.notifySlack("#vforce360-issues", issueUrl);
        }
    }
}
