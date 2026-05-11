package com.example.service;

import com.example.ports.SlackNotifier;

/**
 * Service to handle defect reporting logic.
 * This is the implementation under test.
 */
public class DefectReportService {

    private final SlackNotifier slackNotifier;

    public DefectReportService(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(String defectId, String title) {
        // Placeholder logic to fail the test initially (Red phase)
        String body = "Defect reported: " + title;
        slackNotifier.notify(body);
    }
}
