package com.example.domain.vforce360;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event representing the successful reporting of a defect including GitHub and Slack integration.
 */
public class DefectReportedEvent implements DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String githubUrl;
    private final String slackBody;
    private final Instant occurredAt;

    public DefectReportedEvent(String aggregateId, String githubUrl, String slackBody, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.githubUrl = githubUrl;
        this.slackBody = slackBody;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getSlackBody() {
        return slackBody;
    }
}
