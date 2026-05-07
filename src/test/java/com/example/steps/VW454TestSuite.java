package com.example.steps;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Test Suite for VW-454 Regression.
 * Configures the Spring Test Context to use Mock Adapters.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasses(VW454Steps.class)
@SpringBootTest(classes = VW454TestSuite.TestConfig.class)
@Cucumber
public class VW454TestSuite {

    @Configuration
    static class TestConfig {
        
        @Bean
        @Primary
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackNotificationPort();
        }
    }
}