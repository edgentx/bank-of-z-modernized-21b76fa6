package com.example.steps;

import com.example.Application;
import com.example.mocks.MockSlackNotifier;
import com.example.ports.SlackNotifier;
import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration for VW-454 regression tests.
 * Replaces the real SlackNotifier with the Mock adapter.
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
    }
}
