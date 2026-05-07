package com.example.mocks;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures sent messages to verify body content in tests.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {
    public final List<DefectReportedEvent> sentEvents = new ArrayList<>();

    @Override
    public void sendNotification(DefectReportedEvent event) {
        this.sentEvents.add(event);
    }

    public void reset() {
        sentEvents.clear();
    }
}
