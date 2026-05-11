package com.example.service;

import com.example.ports.SlackNotifier;

/**
 * Service to handle defect reporting logic.
 * This implementation formats the Slack body to include the GitHub Issue URL.
 */
public class DefectReportService {

    private final SlackNotifier slackNotifier;
    private static final String GITHUB_ISSUE_BASE_URL = "https://github.com/example-bank/issues/";

    public DefectReportService(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(String defectId, String title) {
        // Construct the GitHub Issue URL based on the defect ID
        String githubUrl = GITHUB_ISSUE_BASE_URL + defectId;

        // Format the Slack body including the URL
        // Matches the expected assertion: capturedBody.contains("GitHub issue:")
        String body = "Defect reported: " + title + "\n" +
                      "GitHub issue: " + githubUrl;

        slackNotifier.notify(body);
    }
}