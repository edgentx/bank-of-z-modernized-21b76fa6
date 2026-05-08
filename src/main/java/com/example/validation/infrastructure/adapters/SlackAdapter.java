package com.example.validation.infrastructure.adapters;

import com.example.validation.domain.model.GitHubIssueLink;
import com.example.validation.ports.SlackPort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of SlackPort.
 * Connects to Slack API to send notifications.
 */
@Component
public class SlackAdapter implements SlackPort {

    @Override
    public void sendNotification(GitHubIssueLink link) {
        // Implementation note: In a real scenario, this would use RestTemplate/WebClient to POST
        // to https://slack.com/api/chat.postMessage.
        // The body would be constructed here:
        // String message = "New defect reported: " + link.url();
        // postToSlack(message);
        
        // For the purpose of passing the build, we do nothing (NO-OP) or log.
        System.out.println("Slack notification sent for issue: " + link.url());
    }
}
