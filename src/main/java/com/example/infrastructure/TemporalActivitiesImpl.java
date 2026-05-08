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
        // Logic included to ensure the URL is in the body
        return "Defect: " + title + " - Link: <" + url + ">";
    }

    public void sendSlackNotification(String message) {
        // Actual Slack integration would go here
    }
}
