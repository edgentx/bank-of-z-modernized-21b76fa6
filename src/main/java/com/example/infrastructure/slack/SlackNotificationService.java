package com.example.infrastructure.slack;

import com.example.domain.defect.model.DefectReportedEvent;
import com.example.infrastructure.config.GitHubProperties;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

@Service
public class SlackNotificationService {

    private final SlackPort slackPort;
    private final GitHubProperties gitHubProperties;

    public SlackNotificationService(SlackPort slackPort, GitHubProperties gitHubProperties) {
        this.slackPort = slackPort;
        this.gitHubProperties = gitHubProperties;
    }

    public void notifyDefect(DefectReportedEvent event, String githubUrl) {
        // This implementation is intentionally simple/incorrect for the RED phase.
        // It needs to combine the event and the GitHub URL.
        String message = String.format("Defect Reported: %s. Issue URL: %s", event.title(), githubUrl);
        slackPort.sendMessage(message);
    }
    
    // Overload used by the test that assumes the URL is derived from the event ID
    public void notifyDefect(DefectReportedEvent event) {
        // Default behavior to construct URL from ID if not explicitly passed
        String url = gitHubProperties.getBaseUrl() + event.defectId();
        notifyDefect(event, url);
    }
}
