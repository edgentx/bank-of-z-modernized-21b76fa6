package com.vforce360.mocks;

import com.vforce360.model.ModernizationAssessmentReport;
import com.vforce360.ports.ReportRendererPort;

import java.util.Map;

/**
 * Mock implementation of the Renderer Port.
 * In a real test, this might be a Mockito spy, but as a TDD Red Phase artifact,
 * we can implement a 'dumb' version that we expect to fail or be replaced,
 * or use this to verify the Service calls the renderer.
 * 
 * For the purpose of the Red Phase test, we will rely on Mockito to return
 * specific strings, but this class serves as the structural contract.
 */
public class MockReportRenderer implements ReportRendererPort {

    @Override
    public String toMarkdown(ModernizationAssessmentReport report) {
        // Intentionally returning raw JSON string to simulate defect for negative testing
        // or structure verification.
        return "# " + report.getTitle() + "\n" + report.getContent().toString();
    }

    @Override
    public String toHtml(String markdown) {
        return "<html>" + markdown + "</html>";
    }
	
	@Override
    public String toHtml(ModernizationAssessmentReport report) {
        // Intentionally returning raw JSON string to simulate defect
        return report.getContent().toString();
    }
}
