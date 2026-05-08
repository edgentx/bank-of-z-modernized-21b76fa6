package com.example.configuration;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.workflows.ReportDefectActivities;
import com.example.workflows.ReportDefectActivitiesImpl;
import com.example.workflows.ReportDefectWorkflow;
import com.example.workflows.ReportDefectWorkflowImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Defect Reporting components.
 * Wires the real adapters to the Temporal Activities.
 */
@Configuration
public class DefectReportingConfiguration {

    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapter();
    }

    @Bean
    public SlackPort slackPort() {
        return new SlackAdapter();
    }

    @Bean
    public ReportDefectActivities reportDefectActivities(GitHubPort gitHubPort, SlackPort slackPort) {
        return new ReportDefectActivitiesImpl(gitHubPort, slackPort);
    }
}
