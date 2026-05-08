package com.example.application;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DefectReportingActivities {
    @ActivityMethod
    String createGitHubIssue(String title, String body);

    @ActivityMethod
    void notifySlack(String channel, String message);
}
