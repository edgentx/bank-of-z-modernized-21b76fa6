package com.vforce360.unit;

import com.vforce360.MarService;
import com.vforce360.ports.MarPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MarService.
 * Uses Mockito to simulate database responses.
 */
class MarServiceTest {

    private MarPort marPort;
    private MarService marService;

    // The raw JSON payload stored in the database (the source of the bug)
    private static final String RAW_JSON_MAR = """
            {
              "assessmentId": "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
              "summary": "Legacy mainframe critical path identified.",
              "recommendations": ["Refactor CICS", "Migrate DB2"]
            }
            """;

    @BeforeEach
    void setUp() {
        marPort = Mockito.mock(MarPort.class);
        marService = new MarService(marPort);
    }

    @Test
    @DisplayName("RED Phase Test: Should return formatted HTML, not raw JSON")
    void testMarDisplaysFormattedContent() {
        // Arrange
        String projectId = "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1";
        when(marPort.getMarContent(projectId)).thenReturn(RAW_JSON_MAR);

        // Act
        String result = marService.getFormattedReport(projectId);

        // Assert
        // 1. The result should NOT be the raw JSON
        assertThat(result).isNotEqualTo(RAW_JSON_MAR);

        // 2. The result should contain HTML tags (rendering)
        assertThat(result).contains("<html>");
        assertThat(result).contains("<body>");

        // 3. The actual data from the JSON should be visible in the HTML
        assertThat(result).contains("Legacy mainframe critical path identified");
        assertThat(result).contains("Refactor CICS");

        // 4. It should NOT look like a raw JSON object structure at the top level
        assertThat(result).doesNotStartWith("{");
    }

    @Test
    @DisplayName("Should handle missing projects gracefully")
    void testMarNotFound() {
        String projectId = "unknown-project";
        when(marPort.getMarContent(projectId)).thenThrow(new IllegalArgumentException("Project not found"));

        assertThatThrownBy(() -> marService.getFormattedReport(projectId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project not found");
    }
}
