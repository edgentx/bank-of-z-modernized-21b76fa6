package com.example.config;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.workflows.ReportDefectWorkflow;
import com.example.workflows.ReportDefectWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Temporal Workflow worker and client.
 * Registers activities and workflows.
 */
@Configuration
public class TemporalConfig {

    @Bean
    public WorkflowServiceStubs workflowServiceStubs(@Value("${temporal.address}") String address) {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(address).build());
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs,
                WorkflowClientOptions.newBuilder().setNamespace("default").build());
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        return WorkerFactory.newInstance(workflowClient);
    }

    @Bean
    public Worker validationWorker(WorkerFactory factory, 
                                   GitHubAdapter gitHubAdapter, 
                                   SlackAdapter slackAdapter) {
        Worker worker = factory.newWorker("VALIDATION_TASK_QUEUE");
        
        // Register Activities (Adapters)
        worker.registerActivitiesImplementations(gitHubAdapter, slackAdapter);
        
        // Register Workflows
        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        
        return worker;
    }
}
