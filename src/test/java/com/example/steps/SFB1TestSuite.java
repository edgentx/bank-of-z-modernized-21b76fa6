package com.example.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.mocks.MockGitHubMetadataPort;
import com.example.ports.SlackNotificationPort;

/**
 * Test configuration for Cucumber tests.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = com.example.Application.class)
public class SFB1TestSuite {
    // Mocks are managed in the Spring Context via S10TestSuite logic or similar configuration.
    // For this isolated suite, we rely on the steps handling the mocks or a configuration class.
}
