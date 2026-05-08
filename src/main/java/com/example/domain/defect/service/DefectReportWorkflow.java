package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.LogDefectCommand;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import io.temporal.workflow.ActivityStub;
import io.temporal.workflow.Workflow;

@WorkflowInterface
public interface DefectReportWorkflow {

    @WorkflowMethod
    void reportDefect(String defectId, String title, String description);

    @ActivityInterface
    interface SlackActivities {
        @ActivityMethod
        void sendNotification(String message);
    }

    @ActivityInterface
    interface GitHubActivities {
        @ActivityMethod
        String createIssue(String title, String description);
    }

    @ActivityInterface
    interface PersistenceActivities {
        @ActivityMethod
        void saveDefect(DefectAggregate aggregate);
    }
}
