package com.example.application;

import java.util.Objects;

/**
 * Simple Value Object representing a Slack message payload.
 */
public class SlackMessage {
    private final String text;

    public SlackMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlackMessage that = (SlackMessage) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}