package mocks;

import com.vforce360.model.AssessmentReport;
import com.vforce360.ports.MarReportPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock Adapter for MarReportPort.
 * Simulates external dependencies (MongoDB/VForce360 shared) without real I/O.
 */
public class MockMarReportAdapter implements MarReportPort {

    private final Map<String, AssessmentReport> database = new HashMap<>();

    public MockMarReportAdapter() {
        // Initialize with known defect state (Raw JSON)
        AssessmentReport defectiveReport = new AssessmentReport();
        defectiveReport.setId("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1");
        defectiveReport.setFormat("JSON");
        defectiveReport.setRawContent("{\"title\": \"Assessment\", \"status\": \"Critical\"}");
        
        database.put("21b76fa6-afb6-4593-9e1b-b5d7548ac4d1", defectiveReport);
    }

    @Override
    public AssessmentReport getReport(String projectId) {
        // Return predictable data for the test
        return database.get(projectId);
    }
}
