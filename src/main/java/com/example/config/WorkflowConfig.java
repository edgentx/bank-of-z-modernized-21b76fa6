package com.example.config;

import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import com.example.workflow.ReportDefectWorkflow;
import com.example.workflow.ReportDefectWorkflowImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowConfig {

    @Bean
    public ReportDefectWorkflow reportDefectWorkflow(GitHubPort gitHubPort, SlackNotifierPort slackNotifierPort) {
        return new ReportDefectWorkflowImpl(gitHubPort, slackNotifierPort);
    }
}
