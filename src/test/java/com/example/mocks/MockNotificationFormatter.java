package com.example.mocks;

import com.example.domain.reporting.model.DefectReportedEvent;
import com.example.ports.NotificationFormatterPort;

/**
 * Mock implementation of the NotificationFormatterPort.
 * Used in unit tests to simulate the formatting logic without external dependencies.
 * 
 * Note: In TDD Red Phase, this might initially return a stub or empty string.
 * Once the feature is implemented, this class should mirror the actual production logic
 * or the test should use the real production bean via @SpringBootTest.
 */
public class MockNotificationFormatter implements NotificationFormatterPort {

    @Override
    public String formatDefectForSlack(DefectReportedEvent event) {
        // RED PHASE STUB: 
        // Returning empty string or incomplete data to trigger the test failure
        // described in VW-454.
        // return ""; 
        
        // To make the test pass eventually, this logic must be implemented:
        // return String.format("Defect reported: %s - %s", event.defectId(), event.metadata().get("githubUrl"));
        
        throw new RuntimeException("NotImplementedException: Logic missing to inject GitHub URL into Slack body");
    }
}
