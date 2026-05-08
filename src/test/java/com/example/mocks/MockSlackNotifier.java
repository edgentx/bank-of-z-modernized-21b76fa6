package com.example.mocks;

import com.example.ports.SlackNotifierPort;
import com.example.domain.shared.DomainEvent;
import java.util.ArrayList;
import java.util.List;

public class MockSlackNotifier implements SlackNotifierPort {
    public final List<CapturedNotification> notifications = new ArrayList<>();

    @Override
    public void notify(DomainEvent event, String messageBody) {
        notifications.add(new CapturedNotification(event, messageBody));
    }

    public record CapturedNotification(DomainEvent event, String messageBody) {}
}
