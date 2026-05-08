package com.example.domain.slack;

import java.util.StringJoiner;

public class SlackMessage {
    private final String text;

    public SlackMessage(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Slack message text cannot be blank");
        }
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SlackMessage.class.getSimpleName() + "[", "]")
                .add("text='" + text + "'")
                .toString();
    }
}