package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

public class MockSlackPort implements SlackPort {
    public final List<String> notifications = new ArrayList<>();
    public boolean throwException = false;

    @Override
    public void sendDefectNotification(String summary, String githubIssueUrl) {
        if (throwException) {
            throw new RuntimeException("Slack API unavailable");
        }
        // We store the combination to validate the body content effectively
        notifications.add("Summary: " + summary + " | URL: " + githubIssueUrl);
    }

    public boolean wasCalledWith(String summary, String url) {
        return notifications.contains("Summary: " + summary + " | URL: " + url);
    }
}
