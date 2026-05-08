package com.example.workflows;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    void reportDefect(String defectId, String summary, String githubUrl);
}
