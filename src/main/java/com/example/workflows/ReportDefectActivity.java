package com.example.workflows;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ReportDefectActivity {
    @ActivityMethod
    String reportDefectToSlack(String message, String githubUrl);
}
