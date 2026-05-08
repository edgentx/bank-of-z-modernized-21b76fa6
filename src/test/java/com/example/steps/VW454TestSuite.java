package com.example.steps;

import com.example.Application;
import com.example.adapters.RealGitHubAdapter;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.GitHubIssueTracker;
import com.example.ports.SlackNotifier;
import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration for VW-454 regression tests.
 * Replaces the real SlackNotifier with the Mock adapter.
 * Uses a Stub for GitHub to return a predictable URL.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = {Application.class, VW454TestSuite.TestConfig.class})
@Cucumber
public class VW454TestSuite {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SlackNotifier slackNotifier() {
            return new MockSlackNotifier();
        }

        @Bean
        public GitHubIssueTracker gitHubIssueTracker() {
            // Stub implementation that returns a dummy but valid GitHub URL
            return new GitHubIssueTracker() {
                @Override
                public String createIssue(String title, String body, String... labels) {
                    return "https://github.com/example-bank/validation-service/issues/454";
                }
            };
        }
    }
}
