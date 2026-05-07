package com.example.workers;

import io.temporal.activity.ActivityInterface;

import java.util.Map;

@ActivityInterface
public interface ReportDefectActivity {
    String createIssue(String title, String description);
    void notifySlack(String channel, String message);
}
