package com.example.domain.vforce360.service;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.services.DefectReportingService;
import io.temporal.workflow.Workflow;

public class VForce360WorkflowImpl implements VForce360Workflow {
    private final DefectReportingService service;

    // In Temporal, Workflow implementations often need to be instantiated by the worker
    // Assuming activity stubs or direct injection depending on setup.
    // For this compilation fix, we assume a constructor or ActivityStub usage.
    public VForce360WorkflowImpl(DefectReportingService service) {
        this.service = service;
    }

    @Override
    public void reportDefect(ReportDefectCmd cmd) {
        // Workflow logic that delegates to the service
        service.reportDefect(cmd);
    }
}
