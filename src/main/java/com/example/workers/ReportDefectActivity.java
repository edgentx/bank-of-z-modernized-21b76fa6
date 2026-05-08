package com.example.workers;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ReportDefectActivity {
    @ActivityMethod
    String sendToSlack(String title, String description, String githubUrl);
}
