package com.example.domain.validation.port;

/**
 * Port interface for Slack integration.
 * Used by the domain to post messages without depending on concrete implementations.
 */
public interface SlackPort {
    void postMessage(String text);
}
