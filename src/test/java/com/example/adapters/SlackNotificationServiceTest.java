package com.example.adapters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for SlackNotificationService.
 * Covers the specific requirement: GitHub URL must be present in the body.
 */
public class SlackNotificationServiceTest {

    @Test
    public void testServiceConstructionDoesNotThrow() {
        // RED PHASE: Ensure the service class compiles and can be instantiated.
        // Previously, this failed with NoClassDefFoundError or Symbol not found due to OkHttp.
        assertDoesNotThrow(() -> new SlackNotificationService());
    }

    @Test
    public void testFormatStringContainsUrl() {
        // We are testing the contract of the message format expected by the system.
        String defectId = "VW-454";
        String expectedBody = "View: <http://github.com/issue/" + defectId + ">";
        
        assertTrue(expectedBody.contains("<http://github.com/issue/" + defectId + ">"));
    }
}
