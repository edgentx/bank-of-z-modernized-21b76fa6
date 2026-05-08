package com.example.ports;

import com.example.domain.slack.SlackMessage;

public interface SlackNotifier {
    void send(SlackMessage message);
}
