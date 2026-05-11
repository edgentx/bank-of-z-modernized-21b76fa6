package com.example.adapters;

import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.SlackNotifier;
import io.temporal.activity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemporalActivityDefectAdapter {

    @Autowired
    private SlackNotifier slackNotifier;

    public void reportDefect(String defectId, String title, String description, String githubUrl) {
        // Simulate logic to create message body
        String message = "Defect Reported: " + title + "\nGitHub: " + githubUrl;
        try {
            slackNotifier.send(message);
        } catch (Exception e) {
            Activity.wrap(e); // Temporal activity error handling
        }
    }
}
