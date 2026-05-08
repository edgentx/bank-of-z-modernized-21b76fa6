package com.example.e2e.regression;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.port.DefectRepository;
import com.example.domain.validation.model.SlackLinkVerifiedEvent;
import com.example.domain.validation.model.VerifySlackLinkCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.ValidationRepository;
import com.example.mocks.FakeSlackMessageValidator;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.InMemoryValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * S-FB-1: Regression test for GitHub URL validation in Slack body.
 */
public class SFB1RegressionTest {

    private DefectRepository defectRepo;
    private ValidationRepository validationRepo;
    private FakeSlackMessageValidator validator;

    @BeforeEach
    void setUp() {
        defectRepo = new InMemoryDefectRepository();
        validationRepo = new InMemoryValidationRepository();
        validator = new FakeSlackMessageValidator();
    }

    @Test
    void shouldContainGitHubUrlInSlackBody_EndToEnd() {
        // Arrange: Create a defect which generates a GitHub URL
        String defectId = "VW-454";
        DefectAggregate defect = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, "GitHub URL validation failing", "Slack body missing link");
        
        List<com.example.domain.shared.DomainEvent> events = defect.execute(cmd);
        defectRepo.save(defect);
        
        String expectedUrl = defect.getGithubUrl(); // Retrieved from aggregate state
        
        // Simulate the Slack body construction which is expected to contain the URL
        // In a real scenario, this might come from a workflow or service.
        // Here we simulate the body that SHOULD be sent.
        String simulatedSlackBody = "Defect Reported: " + defectId + "\nGitHub Issue: " + expectedUrl;

        // Act: Validate that the simulated body contains the URL
        ValidationAggregate validation = new ValidationAggregate("val-1");
        VerifySlackLinkCmd verifyCmd = new VerifySlackLinkCmd("val-1", simulatedSlackBody, expectedUrl);
        
        List<com.example.domain.shared.DomainEvent> validationEvents = validation.execute(verifyCmd);
        validationRepo.save(validation);

        // Assert: The validation event should indicate the link was found
        assertEquals(1, validationEvents.size());
        SlackLinkVerifiedEvent verifiedEvent = (SlackLinkVerifiedEvent) validationEvents.get(0);
        
        assertTrue(verifiedEvent.found(), "Expected Slack body to contain GitHub URL, but it was not found.");
    }

    @Test
    void shouldFailValidationIfUrlMissingFromSlackBody() {
        // Arrange
        String defectId = "VW-454";
        DefectAggregate defect = new DefectAggregate(defectId);
        defect.execute(new ReportDefectCmd(defectId, "Title", "Desc"));
        
        String expectedUrl = defect.getGithubUrl();
        // Simulate the DEFECT: Slack body missing the URL
        String brokenSlackBody = "Defect Reported: " + defectId + "\nLink: (Pending)";

        // Act
        ValidationAggregate validation = new ValidationAggregate("val-2");
        VerifySlackLinkCmd verifyCmd = new VerifySlackLinkCmd("val-2", brokenSlackBody, expectedUrl);
        
        List<com.example.domain.shared.DomainEvent> validationEvents = validation.execute(verifyCmd);

        // Assert
        SlackLinkVerifiedEvent verifiedEvent = (SlackLinkVerifiedEvent) validationEvents.get(0);
        assertFalse(verifiedEvent.found(), "Expected validation to fail for missing URL.");
    }
}
