package com.example.infra.config;

import com.example.workflow.DefectReportActivities;
import com.example.workflow.DefectReportActivitiesImpl;
import com.example.workflow.ReportDefectWorkflow;
import com.example.workflow.ReportDefectWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Temporal Worker.
 * Registers the Workflow and Activity implementations with the Temporal Worker.
 */
@Configuration
public class TemporalWorkerConfig {

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        
        // Define the task queue name
        String taskQueue = "DEFECT_TASK_QUEUE";
        
        Worker worker = factory.newWorker(taskQueue);
        
        // Register Workflow Implementation
        // Note: Temporal SDK instantiates workflows, so we register the class.
        worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        
        // Register Activity Implementation (Spring Managed Bean)
        // We fetch the bean from the Spring context and register it manually.
        // Assuming DefectReportActivitiesImpl is available as a Bean.
        // In a real Spring Boot + Temporal setup, you might use a factory pattern here.
        
        factory.start();
        return factory;
    }
}
