package com.example.config;

import com.example.domain.defect.DefectReportingWorkflow;
import com.example.domain.defect.DefectReportingWorkflowImpl;
import com.example.domain.defect.ReportDefectActivity;
import com.example.domain.defect.ReportDefectActivityImpl;
import com.example.ports.SlackNotifier;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot Configuration for Temporal Workers.
 * Registers the Workflow and Activity implementations with the Temporal Worker.
 */
@Configuration
public class TemporalWorkerConfiguration {

    /**
     * Configures the Temporal Worker factory.
     * We register the Activity implementation manually to inject Spring dependencies.
     */
    @Bean
    public WorkerFactory workerFactory(io.temporal.serviceclient.WorkflowServiceStubs serviceStubs,
                                       SlackNotifier slackNotifier) {
        
        WorkerFactory factory = WorkerFactory.newInstance(serviceStubs);
        
        // Define the Task Queue name matching the test expectations
        String taskQueue = "VFORCE360_TASK_QUEUE";
        
        Worker worker = factory.newWorker(taskQueue);
        
        // Register Workflow Implementation
        worker.registerWorkflowImplementationTypes(DefectReportingWorkflowImpl.class);
        
        // Register Activity Implementation with constructor injection
        worker.registerActivitiesImplementations(new ReportDefectActivityImpl(slackNotifier));
        
        // Start the worker (optional in some contexts, but explicit here)
        factory.start();
        
        return factory;
    }
}