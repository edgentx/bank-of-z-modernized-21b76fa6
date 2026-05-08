package com.example.infrastructure;

import com.example.domain.shared.SlackMessageValidator;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(taskQueue = "DefectTaskQueue")
public class TemporalActivitiesImpl {

    private final SlackMessageValidator validator;

    public TemporalActivitiesImpl(SlackMessageValidator validator) {
        this.validator = validator;
    }

    public String generateSlackBody(String title, String url) {
        // Simulate logic that might fail VW-454
        String body = "Issue: " + title + " - " + url;
        return body;
    }

    public void sendSlackNotification(String message) {
        // Actual Slack integration would go here
    }
}
