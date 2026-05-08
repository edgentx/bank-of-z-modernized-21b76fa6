package com.example.configuration;

import com.example.domain.shared.SlackMessageValidator;
import com.example.infrastructure.SlackMessageValidatorImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class DefectReportingConfiguration {

    @Bean
    public SlackMessageValidator slackMessageValidator() {
        return new SlackMessageValidatorImpl();
    }

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newInstance();
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        return WorkflowClient.newInstance(workflowServiceStubs);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowServiceStubs workflowServiceStubs) {
        return WorkerFactory.newInstance(workflowServiceStubs);
    }
}