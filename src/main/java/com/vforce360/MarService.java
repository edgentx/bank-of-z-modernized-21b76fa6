package com.vforce360;

import com.vforce360.ports.MarPort;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing and formatting MAR data.
 * NOTE: This file represents the "Empty Implementation" phase.
 * It currently contains logic that causes the test to fail (TDD Red Phase).
 */
@Service
public class MarService {

    private final MarPort marPort;

    public MarService(MarPort marPort) {
        this.marPort = marPort;
    }

    /**
     * Retrieves the formatted MAR report for the CEO to review.
     *
     * @param projectId The unique identifier of the project.
     * @return HTML formatted string ready for display.
     */
    public String getFormattedReport(String projectId) {
        // RED PHASE INTENTIONAL BUG:
        // Currently returning raw JSON instead of formatted HTML.
        // This simulates the defect described in the story.
        return marPort.getMarContent(projectId);
    }
}
