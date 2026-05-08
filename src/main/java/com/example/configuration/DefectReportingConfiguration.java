package com.example.configuration;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.workers.ReportDefectActivity;
import com.example.workers.ReportDefectActivityImpl;
import com.example.workflows.ReportDefectWorkflow;
import com.example.workflows.ReportDefectWorkflowImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectReportingConfiguration {

    @Bean
    public ReportDefectActivity reportDefectActivity(GitHubPort gitHubPort, SlackPort slackPort) {
        return new ReportDefectActivityImpl(gitHubPort, slackPort);
    }

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(ReportDefectActivity activity) {
        return new ReportDefectWorkflowImpl(activity);
    }
}