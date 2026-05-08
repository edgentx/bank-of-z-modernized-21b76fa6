package com.example.domain.vforce360.ports;

/**
 * Port for sending notifications to external systems like Slack.
 */
public interface VForce360NotifierPort {
    void sendDefectReport(String body);
}
