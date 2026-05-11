package com.example.config;

import com.example.domain.defect.adapter.GitHubIssueTrackerAdapter;
import com.example.domain.defect.adapter.SlackNotifier;
import com.example.domain.defect.adapter.impl.GitHubIssueTrackerAdapter;
import com.example.domain.defect.service.DefectReportWorkflow;
import com.example.domain.defect.service.DefectReportWorkflow.PersistenceActivities;
import com.example.infrastructure.adapters.RealSlackNotifier;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Defect Reporting.
 * Registers Temporal Workers and Activities.
 */
@Configuration
public class DefectReportConfig {

    // 1. Implementations
    
    @Bean
    public GitHubIssueTrackerAdapter gitHubIssueTrackerAdapter() {
        return new GitHubIssueTrackerAdapter();
    }

    @Bean
    public SlackNotifier slackNotifier() {
        return new RealSlackNotifier();
    }

    // 2. Temporal Activity Registration
    
    @Bean
    public PersistenceActivities defectActivities(
            GitHubIssueTrackerAdapter gitHubAdapter, 
            SlackNotifier slackNotifier) {
        return new PersistenceActivities() {
            @Override
            public String createGitHubIssue(String title, String description) {
                return gitHubAdapter.createIssue(title, description);
            }

            @Override
            public void linkGitHubIssue(String defectId, String url) {
                // In a real app, this would persist the LinkGitHubIssueCmd event
                // For this E2E validation, we treat this as a passthrough/no-op
            }

            @Override
            public void sendSlackNotification(String title, String url) {
                slackNotifier.notify(title, url);
            }
        };
    }

    // 3. Temporal Worker Starter (Simulated for Spring Boot startup)
    
    @Bean
    public WorkerFactory defectWorkerFactory(WorkflowClient workflowClient, PersistenceActivities activities) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        
        Worker worker = factory.newWorker("DEFECT_REPORT_TASK_QUEUE");
        worker.registerWorkflowImplementationFactory(DefectReportWorkflow.class, () -> new DefectReportWorkflow());
        worker.registerActivitiesImplementations(activities);
        
        factory.start();
        return factory;
    }
}
