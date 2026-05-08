package com.example.infrastructure;

import com.example.domain.vforce360.ReportDefectCommand;
import io.temporal.activity.ActivityInterface;

/**
 * Temporal Activity Interface.
 * Defines the activities that can be invoked by a Temporal Workflow.
 */
@ActivityInterface
public interface VForce360Activities {
    void reportDefect(ReportDefectCommand cmd);
}
