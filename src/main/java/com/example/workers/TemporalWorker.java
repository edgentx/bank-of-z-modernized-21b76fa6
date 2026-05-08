package com.example.workers;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class TemporalWorker {

    private final WorkflowServiceStubs workflowServiceStubs;
    private final WorkflowClient workflowClient;
    private final WorkerFactory workerFactory;

    public TemporalWorker(WorkflowServiceStubs workflowServiceStubs, WorkflowClient workflowClient, WorkerFactory workerFactory) {
        this.workflowServiceStubs = workflowServiceStubs;
        this.workflowClient = workflowClient;
        this.workerFactory = workerFactory;
    }

    @PostConstruct
    public void startWorker() {
        Worker worker = workerFactory.newWorker("DEFECT_TASK_QUEUE");
        // Register workflows here
        workerFactory.start();
    }

    @PreDestroy
    public void stopWorker() {
        workerFactory.shutdown();
        workflowServiceStubs.shutdown();
    }
}
