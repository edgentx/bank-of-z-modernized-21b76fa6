package com.example.steps;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.workflows.ReportDefectActivity;
import com.example.workflows.ReportDefectActivityImpl;
import com.example.workflows.ReportDefectWorkflow;
import com.example.workflows.ReportDefectWorkflowImpl;
import com.example.application.DefectReportService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
public class CucumberSpringConfig {

    @Bean
    public GitHubPort gitHubPort() {
        return Mockito.mock(GitHubPort.class);
    }

    @Bean
    public SlackPort slackPort() {
        return Mockito.mock(SlackPort.class);
    }

    @Bean
    public ReportDefectActivity reportDefectActivity(GitHubPort gitHubPort, SlackPort slackPort) {
        return new ReportDefectActivityImpl(gitHubPort, slackPort);
    }

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(ReportDefectActivity activity) {
        // Simple wrapper for testing the workflow logic without a Temporal server
        return new ReportDefectWorkflowImpl(activity);
    }

    @Bean
    public DefectReportService defectReportService(ReportDefectWorkflow workflow, GitHubPort gitHubPort, SlackPort slackPort) {
        return new DefectReportService(workflow, gitHubPort, slackPort);
    }
}
