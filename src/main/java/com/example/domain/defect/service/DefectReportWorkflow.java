package com.example.domain.defect.service;

import com.example.domain.defect.adapter.GitHubIssueTrackerAdapter;
import com.example.domain.defect.adapter.SlackNotifier;
import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.GitHubIssueLinkedEvent;
import com.example.domain.defect.model.LinkGitHubIssueCmd;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.DomainEvent;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Temporal Workflow implementation for Defect Reporting.
 * Orchestrates creating a GitHub issue and sending a Slack notification.
 */
@WorkflowImpl(taskQueue = "DEFECT_REPORT_TASK_QUEUE")
public class DefectReportWorkflow implements DefectReportWorkflowInterface {

    private final PersistenceActivities activities;

    // Public constructor for Temporal factory
    public DefectReportWorkflow() {
        this.activities = Workflow.newActivityStub(PersistenceActivities.class);
    }

    @Override
    public String reportDefect(String defectId, String title, String description) {
        // 1. Execute ReportDefectCommand on Aggregate
        // In a real scenario, we might load from DB or create new.
        // Based on test steps, we assume creation.
        
        // NOTE: Pure domain logic execution happens in activities or here. 
        // For simplicity in this flow, we'll do the orchestration here.
        
        // 1. Create GitHub Issue via Adapter
        String issueUrl = activities.createGitHubIssue(title, description);
        
        // 2. Update Domain State (Link Issue)
        // This would normally persist the event. Here we simulate the state change.
        activities.linkGitHubIssue(defectId, issueUrl);
        
        // 3. Notify Slack
        activities.sendSlackNotification(title, issueUrl);
        
        return issueUrl;
    }

    /**
     * Activity Interface for Temporal to interact with Spring Beans/Adapters.
     */
    public interface PersistenceActivities {
        String createGitHubIssue(String title, String description);
        void linkGitHubIssue(String defectId, String url);
        void sendSlackNotification(String title, String url);
        
        // Domain interaction if needed
        // DefectAggregate loadDefect(String id);
    }
}
