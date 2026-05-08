package com.example.domain.ports;

/**
 * Port for validating Slack messages content.
 */
public interface SlackMessageValidator {
    /**
     * Validates that the body contains the required GitHub issue link.
     * @param body The message body to check.
     * @param expectedUrl The expected GitHub URL.
     * @return true if valid, false otherwise.
     */
    boolean validateBodyContainsUrl(String body, String expectedUrl);
}
