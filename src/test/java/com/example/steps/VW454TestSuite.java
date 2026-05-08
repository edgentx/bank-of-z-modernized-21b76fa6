package com.example.steps;

import com.example.mocks.MockGithubAdapter;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotificationPort;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Test Configuration for VW-454 Regression Tests.
 * Wires up the Mock Adapters to the Ports.
 */
@Configuration
@CucumberContextConfiguration
@SpringBootTest(classes = VW454TestSuite.Config.class)
public class VW454TestSuite {

    @Configuration
    static class Config {
        
        @Bean
        @Primary
        public SlackNotificationPort slackNotificationPort() {
            return new MockSlackAdapter();
        }

        @Bean
        @Primary
        public GithubPort githubPort() {
            return new MockGithubAdapter();
        }
    }
}
