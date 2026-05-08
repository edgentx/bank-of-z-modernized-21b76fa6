package com.example.domain.vforce360.service;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.spring.boot.WorkflowImpl;
import org.springframework.stereotype.Component;

@WorkflowInterface
public interface VForce360Workflow {
    @WorkflowMethod
    String reportDefect(String title, String description);
}
