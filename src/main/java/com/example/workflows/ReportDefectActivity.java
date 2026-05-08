package com.example.workflows;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ReportDefectActivity {
    @ActivityMethod
    String generateId();

    @ActivityMethod
    void saveDefect(String id, String title, String description);

    @ActivityMethod
    String createGitHubIssue(String title, String description);

    @ActivityMethod
    void notifySlack(String defectId, String githubUrl);
}
