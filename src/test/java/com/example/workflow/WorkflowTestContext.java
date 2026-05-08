package com.example.workflow;

import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import io.temporal.workflow.WorkflowStub;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WorkflowTestContext {

    private final TestWorkflowEnvironment testEnv;
    private final Worker worker;
    private final ReportDefectWorkflow workflowStub;

    public WorkflowTestContext(
            DefectReportActivitiesImpl activities,
            ReportDefectWorkflowImpl workflowImpl
    ) {
        this.testEnv = TestWorkflowEnvironment.newInstance();
        this.worker = testEnv.newWorker("VFORCE_TASK_QUEUE");
        this.worker.registerWorkflowImplementationTypes(ReportDefectWorkflowImpl.class);
        this.worker.registerActivitiesImplementations(activities);
        testEnv.start();
        
        // Create a stub for the tests
        this.workflowStub = testEnv.newWorkflowStub(
            ReportDefectWorkflow.class, 
            ReportDefectWorkflow.class.getSimpleName()
        );
    }

    public ReportDefectWorkflow getWorkflowStub() {
        return this.workflowStub;
    }

    public void close() {
        testEnv.close();
    }
}