package com.example.config;

import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.workflow.ReportDefectWorkflowImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Ports, Adapters, and Workflow beans.
 */
@Configuration
public class WorkflowConfig {

    @Bean
    public GitHubPort gitHubPort() {
        // In a real scenario, this might use RestTemplate or WebClient properties
        return new GitHubAdapter();
    }

    @Bean
    public SlackPort slackPort() {
        return new SlackAdapter();
    }

    @Bean
    public ReportDefectWorkflowImpl reportDefectWorkflow(GitHubPort gitHubPort, SlackPort slackPort) {
        return new ReportDefectWorkflowImpl(gitHubPort, slackPort);
    }
}