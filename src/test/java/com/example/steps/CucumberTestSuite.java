package com.example.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.ports.*;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Configuration for Cucumber tests in the TDD Red phase.
 * We mock all external ports to ensure tests are deterministic and fast.
 */
@CucumberContextConfiguration
@SpringBootTest
public class CucumberTestSuite {

    // Mock the Slack Notification Port
    @MockBean
    public SlackNotificationPort slackNotificationPort;

    // Mock the GitHub Port
    @MockBean
    public GitHubPort gitHubPort;

    // We can set default mock behaviors here if needed for the whole suite
    // but per-step mocking (in Steps classes) is often more flexible.
}
